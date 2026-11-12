package com.minhafazenda.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhafazenda.enums.AcaoAuditoria;
import com.minhafazenda.entity.AuditoriaFazenda;
import com.minhafazenda.repository.AuditoriaFazendaRepository;
import com.minhafazenda.repository.FazendaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaFazendaRepository registros;
    private final FazendaRepository fazendas;

    @Transactional
    public void registrar(UUID fazendaId, UUID atorId, AcaoAuditoria acao, UUID alvoId) {
        registros.save(new AuditoriaFazenda(fazendas.getReferenceById(fazendaId), atorId, acao, alvoId));
    }
}
