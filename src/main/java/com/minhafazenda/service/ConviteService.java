package com.minhafazenda.service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhafazenda.config.ConviteProperties;
import com.minhafazenda.dto.ConviteResponse;
import com.minhafazenda.enums.AcaoAuditoria;
import com.minhafazenda.entity.Convite;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.repository.ConviteRepository;
import com.minhafazenda.repository.FazendaRepository;
import com.minhafazenda.security.TokenSeguro;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConviteService {

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
    public UUID aceitar(UUID usuarioId, String token) {
        Convite convite = convites.buscarPorTokenHashComLock(TokenSeguro.hash(token))
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
