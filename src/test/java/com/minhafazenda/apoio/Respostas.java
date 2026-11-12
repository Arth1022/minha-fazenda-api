package com.minhafazenda.apoio;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.ResultMatcher;

public final class Respostas {

    private Respostas() {
    }

    /** Erro no formato do contrato: status HTTP, código e uma mensagem para o usuário. */
    public static ResultMatcher erro(int statusHttp, String codigo) {
        return resultado -> {
            status().is(statusHttp).match(resultado);
            jsonPath("$.erro").value(codigo).match(resultado);
            jsonPath("$.mensagem").isNotEmpty().match(resultado);
        };
    }
}
