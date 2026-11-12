package com.minhafazenda.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhafazenda.config.ConviteProperties;
import com.minhafazenda.dto.CodigoConviteResponse;
import com.minhafazenda.dto.ConviteResponse;
import com.minhafazenda.enums.AcaoAuditoria;
import com.minhafazenda.entity.Convite;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.repository.ConviteRepository;
import com.minhafazenda.repository.FazendaRepository;
import com.minhafazenda.security.CodigoConvite;
import com.minhafazenda.security.TokenSeguro;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConviteService {

    static final Duration VALIDADE_DO_CODIGO = Duration.ofHours(24);

    private final ConviteRepository convites;
    private final FazendaRepository fazendas;
    private final MembroService membroService;
    private final AuditoriaService auditoria;
    private final ConviteProperties properties;
    private final Clock relogio;

    /** O token em texto só existe nesta resposta; o banco guarda apenas o SHA-256. */
    @Transactional
    public ConviteResponse criar(UUID atorId, UUID fazendaId) {
        membroService.exigirOwner(atorId, fazendaId);
        String token = TokenSeguro.gerar();
        Instant expiraEm = relogio.instant().plus(properties.validade());
        Convite convite = convites.save(new Convite(fazendas.getReferenceById(fazendaId),
                TokenSeguro.hash(token), atorId, expiraEm, properties.usosMax()));
        auditoria.registrar(fazendaId, atorId, AcaoAuditoria.CONVITE_CRIADO, convite.getId());
        return new ConviteResponse(convite.getId(), properties.urlPara(token), token, expiraEm);
    }

    /**
     * Gera um código de convite: vale 24 horas e para uma pessoa só. Cada pessoa convidada
     * recebe o seu (gerar outro não invalida os anteriores ainda não usados).
     */
    @Transactional
    public CodigoConviteResponse gerarCodigo(UUID atorId, UUID fazendaId) {
        membroService.exigirOwner(atorId, fazendaId);
        Instant agora = relogio.instant();
        String codigo = codigoInedito();
        Convite convite = convites.save(Convite.codigoDaFazenda(fazendas.getReferenceById(fazendaId), codigo,
                TokenSeguro.hash(codigo), atorId, agora.plus(VALIDADE_DO_CODIGO)));
        auditoria.registrar(fazendaId, atorId, AcaoAuditoria.CONVITE_CRIADO, convite.getId());
        return respostaDoCodigo(convite);
    }

    /** O último código que ainda vale e não foi usado (para o dono ver de novo), ou 404. */
    @Transactional(readOnly = true)
    public CodigoConviteResponse codigoValido(UUID atorId, UUID fazendaId) {
        membroService.exigirOwner(atorId, fazendaId);
        return convites.buscarCodigoValido(fazendaId, relogio.instant())
                .map(ConviteService::respostaDoCodigo)
                .orElseThrow(() -> new ApiException(CodigoErro.NAO_ENCONTRADO, "Nenhum código válido. Gere um novo."));
    }

    private String codigoInedito() {
        for (int tentativa = 0; tentativa < 10; tentativa++) {
            String codigo = CodigoConvite.gerar();
            if (!convites.existsByCodigo(codigo)) {
                return codigo;
            }
        }
        throw new IllegalStateException("Não foi possível gerar um código de convite inédito");
    }

    private static CodigoConviteResponse respostaDoCodigo(Convite convite) {
        return new CodigoConviteResponse(convite.getCodigo(),
                "Entre na minha fazenda: " + convite.getCodigo(), convite.getExpiraEm());
    }

    @Transactional
    public void revogar(UUID atorId, UUID conviteId) {
        Convite convite = convites.findById(conviteId)
                .orElseThrow(() -> new ApiException(CodigoErro.NAO_ENCONTRADO));
        UUID fazendaId = convite.getFazenda().getId();
        membroService.exigirOwner(atorId, fazendaId);
        convite.revogar(relogio.instant());
        auditoria.registrar(fazendaId, atorId, AcaoAuditoria.CONVITE_REVOGADO, conviteId);
    }

    /**
     * Quem já é membro recebe a fazenda de volta sem consumir o convite, o que torna seguro
     * o app repetir a chamada depois de uma falha de rede.
     */
    // TODO(decisao): convite com aprovação do owner; hoje a entrada é direta ao aceitar.
    @Transactional
    public UUID aceitar(UUID usuarioId, String tokenOuCodigo) {
        // Código digitado (ignora maiúsculas, espaço e hífen) ou token de link antigo, exato
        Convite convite = CodigoConvite.normalizar(tokenOuCodigo)
                .flatMap(codigo -> convites.buscarPorTokenHashComLock(TokenSeguro.hash(codigo)))
                .or(() -> convites.buscarPorTokenHashComLock(TokenSeguro.hash(tokenOuCodigo)))
                .orElseThrow(() -> new ApiException(CodigoErro.CONVITE_INVALIDO));
        UUID fazendaId = convite.getFazenda().getId();
        if (membroService.ehMembroAtivo(usuarioId, fazendaId)) {
            return fazendaId;
        }
        if (!convite.estaValido(relogio.instant())) {
            throw new ApiException(CodigoErro.CONVITE_INVALIDO);
        }
        convite.consumir();
        membroService.entrarComoCollaborator(fazendaId, usuarioId);
        auditoria.registrar(fazendaId, usuarioId, AcaoAuditoria.CONVITE_ACEITO, convite.getId());
        return fazendaId;
    }
}
