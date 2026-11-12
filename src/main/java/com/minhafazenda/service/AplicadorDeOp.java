package com.minhafazenda.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.minhafazenda.config.PermissoesProperties;
import com.minhafazenda.dto.SyncOp;
import com.minhafazenda.dto.TipoOp;
import com.minhafazenda.entity.FazendaMembro;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.exception.OpRejeitadaException;
import com.minhafazenda.infra.SchemaCache;
import com.minhafazenda.infra.TabelaSchema;
import com.minhafazenda.infra.ValorColuna;
import com.minhafazenda.repository.SyncRepository;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;

/** Aplica uma operação da fila do app: descobre a fazenda, confere a permissão, valida e grava. */
@Component
@RequiredArgsConstructor
class AplicadorDeOp {

    private final SchemaCache schemaCache;
    private final SyncRepository repositorio;
    private final MembroService membroService;
    private final PermissoesProperties permissoes;
    private final List<ValidadorSync> validadores;

    void aplicar(UUID usuarioId, SyncOp op) {
        TabelaSchema tabela = schemaCache.buscar(op.table())
                .orElseThrow(() -> new OpRejeitadaException(CodigoErro.VALIDACAO, "Tabela não sincronizável."));
        UUID id = comoUuid(op.id(), "id");
        Optional<UUID> fazendaDoRegistro = repositorio.buscarFazendaDoRegistro(tabela, id);

        if (fazendaDoRegistro.isEmpty() && op.op() != TipoOp.PUT) {
            exigirQueSejaDelete(op);
            return;
        }
        // A fazenda de um registro existente nunca muda; o fazenda_id do app só vale para criar.
        UUID fazendaId = fazendaDoRegistro.orElseGet(() -> fazendaInformada(op));
        exigirPermissao(usuarioId, fazendaId, op);
        validar(tabela, op);
        gravar(tabela, id, fazendaId, usuarioId, op);
    }

    /** Apagar o que não existe já está feito; alterar o que não existe é erro. */
    private static void exigirQueSejaDelete(SyncOp op) {
        if (op.op() != TipoOp.DELETE) {
            throw new OpRejeitadaException(CodigoErro.NAO_ENCONTRADO, "Registro não encontrado.");
        }
    }

    private static UUID fazendaInformada(SyncOp op) {
        JsonNode fazendaId = op.data().get(TabelaSchema.FAZENDA_ID);
        if (fazendaId == null || !fazendaId.isString()) {
            throw new OpRejeitadaException(CodigoErro.VALIDACAO, "fazenda_id: é obrigatório em registro novo");
        }
        return comoUuid(fazendaId.asString(), TabelaSchema.FAZENDA_ID);
    }

    private void exigirPermissao(UUID usuarioId, UUID fazendaId, SyncOp op) {
        FazendaMembro membro;
        try {
            membro = membroService.exigirMembro(usuarioId, fazendaId);
        } catch (ApiException e) {
            throw new OpRejeitadaException(e.getCodigo(), e.getMessage());
        }
        if (apagaRegistro(op) && !membro.ehOwner() && !permissoes.collaboratorPodeApagar()) {
            throw new OpRejeitadaException(CodigoErro.SEM_PERMISSAO, "Seu papel não permite apagar registros.");
        }
    }

    /** Gravar deleted_at por PUT ou PATCH também é apagar, e respeita a mesma permissão. */
    private static boolean apagaRegistro(SyncOp op) {
        JsonNode deletedAt = op.data().get(TabelaSchema.DELETED_AT);
        return op.op() == TipoOp.DELETE || (deletedAt != null && !deletedAt.isNull());
    }

    private void validar(TabelaSchema tabela, SyncOp op) {
        validadores.stream()
                .filter(validador -> validador.tabela().equals(tabela.nome()))
                .forEach(validador -> validador.validar(op));
    }

    private void gravar(TabelaSchema tabela, UUID id, UUID fazendaId, UUID usuarioId, SyncOp op) {
        List<ValorColuna> valores = tabela.valoresGravaveis(op.data());
        try {
            int linhas = switch (op.op()) {
                case PUT -> repositorio.inserirOuAtualizar(tabela, id, fazendaId, usuarioId, valores);
                case PATCH -> repositorio.atualizar(tabela, id, valores);
                case DELETE -> repositorio.apagar(tabela, id);
            };
            if (linhas == 0) {
                throw new OpRejeitadaException(CodigoErro.SEM_PERMISSAO, CodigoErro.SEM_PERMISSAO.getMensagemPadrao());
            }
        } catch (DataIntegrityViolationException e) {
            throw new OpRejeitadaException(CodigoErro.VALIDACAO, "Dados incompatíveis com a tabela.");
        }
    }

    private static UUID comoUuid(String texto, String campo) {
        try {
            return UUID.fromString(texto);
        } catch (IllegalArgumentException e) {
            throw new OpRejeitadaException(CodigoErro.VALIDACAO, campo + ": deve ser um UUID");
        }
    }
}
