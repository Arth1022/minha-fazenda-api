package com.minhafazenda.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final CodigoErro codigo;

    public ApiException(CodigoErro codigo) {
        this(codigo, codigo.getMensagemPadrao());
    }

    public ApiException(CodigoErro codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }
}
