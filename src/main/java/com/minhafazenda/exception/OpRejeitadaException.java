package com.minhafazenda.exception;

import lombok.Getter;

/**
 * Recusa de uma única operação do lote. Não derruba a requisição: a operação é registrada
 * em sync_rejeicoes e as demais continuam, para a fila do app não travar.
 */
@Getter
public class OpRejeitadaException extends RuntimeException {

    private final CodigoErro codigo;

    public OpRejeitadaException(CodigoErro codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }
}
