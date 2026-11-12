package com.minhafazenda.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.minhafazenda.dto.SyncOp;
import com.minhafazenda.entity.SyncRejeicao;
import com.minhafazenda.exception.OpRejeitadaException;
import com.minhafazenda.repository.SyncRejeicaoRepository;
import com.minhafazenda.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class RegistroDeRejeicoes {

    private final SyncRejeicaoRepository rejeicoes;
    private final UsuarioRepository usuarios;
    private final JsonMapper jsonMapper;

    public void registrar(UUID usuarioId, SyncOp op, OpRejeitadaException rejeicao) {
        String motivo = "%s: %s".formatted(rejeicao.getCodigo(), rejeicao.getMessage());
        rejeicoes.save(new SyncRejeicao(usuarios.getReferenceById(usuarioId), op.table(), op.id(),
                op.op().name(), jsonMapper.writeValueAsString(op.data()), motivo));
    }
}
