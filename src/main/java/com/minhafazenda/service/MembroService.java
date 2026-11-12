package com.minhafazenda.service;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhafazenda.enums.AcaoAuditoria;
import com.minhafazenda.entity.Fazenda;
import com.minhafazenda.entity.FazendaMembro;
import com.minhafazenda.enums.Papel;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.repository.FazendaMembroRepository;
import com.minhafazenda.repository.FazendaRepository;
import com.minhafazenda.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Único ponto de checagem de permissão. Fazenda e papel são lidos de fazenda_membros a cada
 * chamada, nunca do token nem de dados enviados pelo app.
 */
@Service
@RequiredArgsConstructor
public class MembroService {

    private final FazendaMembroRepository membros;
    private final FazendaRepository fazendas;
    private final UsuarioRepository usuarios;
    private final AuditoriaService auditoria;
    private final Clock relogio;

    // Sem @Transactional de propósito: o sync chama este método dentro da própria transação
    // e trata a recusa como rejeição da operação, sem marcar o lote inteiro para rollback.
    public FazendaMembro exigirMembro(UUID usuarioId, UUID fazendaId) {
        return membros.findByFazendaIdAndUsuarioIdAndRemovedAtIsNull(fazendaId, usuarioId)
                .orElseThrow(() -> new ApiException(CodigoErro.SEM_PERMISSAO));
    }

    public FazendaMembro exigirOwner(UUID usuarioId, UUID fazendaId) {
        FazendaMembro membro = exigirMembro(usuarioId, fazendaId);
        if (!membro.ehOwner()) {
            throw new ApiException(CodigoErro.SEM_PERMISSAO);
        }
        return membro;
    }

    public boolean ehMembroAtivo(UUID usuarioId, UUID fazendaId) {
        return membros.findByFazendaIdAndUsuarioIdAndRemovedAtIsNull(fazendaId, usuarioId).isPresent();
    }

    public boolean ehOwnerAtivo(UUID usuarioId, UUID fazendaId) {
        return membros.findByFazendaIdAndUsuarioIdAndRemovedAtIsNull(fazendaId, usuarioId)
                .filter(FazendaMembro::ehOwner)
                .isPresent();
    }

    @Transactional
    public void adicionarOwner(Fazenda fazenda, UUID usuarioId) {
        membros.save(new FazendaMembro(fazenda, usuarios.getReferenceById(usuarioId), Papel.OWNER));
    }

    /** Quem já foi removido volta como collaborator, na mesma linha de fazenda_membros. */
    @Transactional
    public void entrarComoCollaborator(UUID fazendaId, UUID usuarioId) {
        membros.findByFazendaIdAndUsuarioId(fazendaId, usuarioId).ifPresentOrElse(
                membro -> membro.reativarComo(Papel.COLLABORATOR),
                () -> membros.save(new FazendaMembro(fazendas.getReferenceById(fazendaId),
                        usuarios.getReferenceById(usuarioId), Papel.COLLABORATOR)));
    }

    @Transactional
    public void alterarPapel(UUID atorId, UUID fazendaId, UUID alvoId, Papel novoPapel) {
        exigirOwner(atorId, fazendaId);
        FazendaMembro alvo = buscarMembroAtivo(fazendaId, alvoId);
        if (alvo.ehOwner() && novoPapel != Papel.OWNER) {
            exigirOutroOwner(fazendaId);
        }
        alvo.alterarPapel(novoPapel);
        auditoria.registrar(fazendaId, atorId, AcaoAuditoria.PAPEL_ALTERADO, alvoId);
    }

    @Transactional
    public void remover(UUID atorId, UUID fazendaId, UUID alvoId) {
        exigirOwner(atorId, fazendaId);
        desligar(atorId, buscarMembroAtivo(fazendaId, alvoId));
    }

    @Transactional
    public void sair(UUID usuarioId, UUID fazendaId) {
        desligar(usuarioId, exigirMembro(usuarioId, fazendaId));
    }

    private void desligar(UUID atorId, FazendaMembro membro) {
        UUID fazendaId = membro.getFazenda().getId();
        if (membro.ehOwner()) {
            exigirOutroOwner(fazendaId);
        }
        membro.remover(relogio.instant());
        auditoria.registrar(fazendaId, atorId, AcaoAuditoria.MEMBRO_REMOVIDO, membro.getUsuario().getId());
    }

    private FazendaMembro buscarMembroAtivo(UUID fazendaId, UUID usuarioId) {
        return membros.findByFazendaIdAndUsuarioIdAndRemovedAtIsNull(fazendaId, usuarioId)
                .orElseThrow(() -> new ApiException(CodigoErro.NAO_ENCONTRADO));
    }

    private void exigirOutroOwner(UUID fazendaId) {
        if (membros.buscarAtivosPorPapelComLock(fazendaId, Papel.OWNER).size() <= 1) {
            throw new ApiException(CodigoErro.ULTIMO_OWNER);
        }
    }
}
