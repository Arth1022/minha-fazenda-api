package com.minhafazenda.service;

import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;

public final class NomeUsuario {

    private static final int MINIMO = 2;
    private static final int MAXIMO = 60;

    private NomeUsuario() {
    }

    public static String normalizar(String nome) {
        String limpo = nome == null ? "" : nome.strip();
        if (limpo.length() < MINIMO || limpo.length() > MAXIMO) {
            throw new ApiException(CodigoErro.VALIDACAO,
                    "nome: deve ter de %d a %d caracteres".formatted(MINIMO, MAXIMO));
        }
        return limpo;
    }
}
