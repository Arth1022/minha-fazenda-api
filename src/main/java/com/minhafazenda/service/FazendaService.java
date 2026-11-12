package com.minhafazenda.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhafazenda.entity.Fazenda;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.repository.FazendaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FazendaService {

    private final FazendaRepository fazendas;
    private final MembroService membroService;

    /**
     * O id pode vir do app, para a fazenda criada no modo sem conta manter o mesmo id na nuvem.
     * Repetir a criação com o mesmo id é idempotente para quem já é owner dela.
     */
    @Transactional
    public Fazenda criar(UUID usuarioId, UUID idDoApp, String nome) {
        Optional<Fazenda> existente = Optional.ofNullable(idDoApp).flatMap(fazendas::findById);
        if (existente.isPresent()) {
            return exigirMesmoOwner(usuarioId, existente.get());
        }
        UUID id = idDoApp != null ? idDoApp : UUID.randomUUID();
        Fazenda fazenda = fazendas.save(new Fazenda(id, nome, usuarioId));
        membroService.adicionarOwner(fazenda, usuarioId);
        return fazenda;
    }

    @Transactional
    public Fazenda renomear(UUID usuarioId, UUID fazendaId, String novoNome) {
        membroService.exigirOwner(usuarioId, fazendaId);
        Fazenda fazenda = fazendas.findByIdAndDeletedAtIsNull(fazendaId)
                .orElseThrow(() -> new ApiException(CodigoErro.NAO_ENCONTRADO));
        fazenda.renomear(novoNome);
        return fazenda;
    }

    private Fazenda exigirMesmoOwner(UUID usuarioId, Fazenda fazenda) {
        if (!membroService.ehOwnerAtivo(usuarioId, fazenda.getId())) {
            throw new ApiException(CodigoErro.VALIDACAO, "id: já está em uso por outra fazenda");
        }
        return fazenda;
    }
}
