package com.minhafazenda.security;

import java.util.List;

import org.springframework.stereotype.Component;

import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CredencialVerifiers {

    private final List<CredencialVerifier> verifiers;

    public CredencialVerifier para(String tipo) {
        return verifiers.stream()
                .filter(verifier -> verifier.suporta(tipo))
                .findFirst()
                .orElseThrow(() -> new ApiException(CodigoErro.VALIDACAO,
                        "credencial.tipo: tipo de credencial não suportado"));
    }
}
