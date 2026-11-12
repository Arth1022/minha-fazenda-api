package com.minhafazenda.service;

import java.util.Locale;

import org.springframework.util.StringUtils;

import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;

public final class EmailUsuario {

    private EmailUsuario() {
    }

    public static String normalizar(String email) {
        if (!StringUtils.hasText(email)) {
            throw new ApiException(CodigoErro.VALIDACAO, "credencial.email: é obrigatório");
        }
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
