package com.minhafazenda.security;

import static com.minhafazenda.apoio.Respostas.erro;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.Respostas;

@TestPropertySource(properties = "rate-limit.requisicoes-por-minuto=3")
class RateLimitIntegrationTest extends IntegracaoTest {

    private static final String LOGIN = """
            {"credencial": {"tipo": "dev", "email": "ninguem@teste.com"}, "deviceId": "aparelho"}
            """;

    @Autowired
    private MockMvc mvc;

    @Test
    void bloqueiaOIpQuePassaDoLimiteNasRotasDeConta() throws Exception {
        for (int tentativa = 0; tentativa < 3; tentativa++) {
            mvc.perform(loginDoIp("10.0.0.1")).andExpect(status().isNotFound());
        }

        mvc.perform(loginDoIp("10.0.0.1")).andExpect(erro(429, "MUITAS_TENTATIVAS"));
        mvc.perform(loginDoIp("10.0.0.2")).andExpect(status().isNotFound());
    }

    @Test
    void limitaTambemOAceiteDeConvite() throws Exception {
        for (int tentativa = 0; tentativa < 3; tentativa++) {
            mvc.perform(doIp(post("/convites/aceitar"), "10.0.0.3")).andExpect(status().isUnauthorized());
        }

        mvc.perform(doIp(post("/convites/aceitar"), "10.0.0.3")).andExpect(erro(429, "MUITAS_TENTATIVAS"));
    }

    @Test
    void naoLimitaAsDemaisRotas() throws Exception {
        for (int tentativa = 0; tentativa < 6; tentativa++) {
            mvc.perform(doIp(get("/usuarios/me"), "10.0.0.4")).andExpect(erro(401, "TOKEN_INVALIDO"));
        }
    }

    private static MockHttpServletRequestBuilder loginDoIp(String ip) {
        return doIp(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(LOGIN), ip);
    }

    private static MockHttpServletRequestBuilder doIp(MockHttpServletRequestBuilder requisicao, String ip) {
        return requisicao.with(servletRequest -> {
            servletRequest.setRemoteAddr(ip);
            return servletRequest;
        });
    }
}
