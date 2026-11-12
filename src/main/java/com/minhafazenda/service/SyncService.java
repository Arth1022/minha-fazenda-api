package com.minhafazenda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhafazenda.config.SyncProperties;
import com.minhafazenda.dto.OpRejeitada;
import com.minhafazenda.dto.SyncOp;
import com.minhafazenda.dto.SyncUploadRequest;
import com.minhafazenda.dto.SyncUploadResponse;
import com.minhafazenda.entity.FazendaMembro;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.exception.OpRejeitadaException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final SyncProperties properties;
    private final AplicadorDeOp aplicador;
    private final RegistroDeRejeicoes rejeicoes;

    /**
     * Aplica o lote em ordem, numa transação. Operação recusada vira rejeição e o lote segue;
     * erro inesperado desfaz tudo e o app reenvia o lote inteiro.
     */
    @Transactional
    public SyncUploadResponse aplicar(UUID usuarioId, SyncUploadRequest request) {
        if (request.schemaVersion() < properties.minSchemaVersion()) {
            throw new ApiException(CodigoErro.APP_DESATUALIZADO);
        }
        int aplicadas = 0;
        List<OpRejeitada> rejeitadas = new ArrayList<>();
        Map<UUID, FazendaMembro> membros = new HashMap<>();
        for (SyncOp op : request.ops()) {
            try {
                aplicador.aplicar(usuarioId, op, membros);
                aplicadas++;
            } catch (OpRejeitadaException rejeicao) {
                rejeicoes.registrar(usuarioId, op, rejeicao);
                rejeitadas.add(new OpRejeitada(op.table(), op.id(), rejeicao.getCodigo().name(), rejeicao.getMessage()));
            }
        }
        return new SyncUploadResponse(aplicadas, rejeitadas);
    }
}
