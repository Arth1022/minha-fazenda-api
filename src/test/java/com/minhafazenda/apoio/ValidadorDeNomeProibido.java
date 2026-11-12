package com.minhafazenda.apoio;

import com.minhafazenda.dto.SyncOp;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.exception.OpRejeitadaException;
import com.minhafazenda.service.ValidadorSync;

import tools.jackson.databind.JsonNode;

/** Validador de exemplo, para testar que o sync consulta o validador da tabela. */
public class ValidadorDeNomeProibido implements ValidadorSync {

    public static final String NOME_PROIBIDO = "NOME_PROIBIDO_PELO_VALIDADOR";

    @Override
    public String tabela() {
        return "animais";
    }

    @Override
    public void validar(SyncOp op) {
        JsonNode nome = op.data().get("nome");
        if (nome != null && NOME_PROIBIDO.equals(nome.asString())) {
            throw new OpRejeitadaException(CodigoErro.VALIDACAO, "nome: não é permitido");
        }
    }
}
