package com.minhafazenda.exception;

public record ErroResponse(String erro, String mensagem) {

    public static ErroResponse de(CodigoErro codigo) {
        return new ErroResponse(codigo.name(), codigo.getMensagemPadrao());
    }

    public static ErroResponse de(ApiException excecao) {
        return new ErroResponse(excecao.getCodigo().name(), excecao.getMessage());
    }
}
