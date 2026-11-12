package com.minhafazenda.controller;

import static com.minhafazenda.apoio.Respostas.erro;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.minhafazenda.apoio.ApiDeTeste;
import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.Respostas;

import tools.jackson.databind.JsonNode;

class SessaoIntegrationTest extends IntegracaoTest {

    @Test
    void loginDevolveTokensEUsuario() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.post("/auth/login", corpoDeLogin(conta.email(), "outro-aparelho"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").value(1800))
                .andExpect(jsonPath("$.usuario.id").value(conta.usuarioId().toString()));
    }

    @Test
    void loginIgnoraMaiusculasNoEmail() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.post("/auth/login", corpoDeLogin(conta.email().toUpperCase(), "aparelho"))
                .andExpect(status().isOk());
    }

    @Test
    void loginDeContaInexistenteRespondeNaoEncontrado() throws Exception {
        api.post("/auth/login", corpoDeLogin(ApiDeTeste.novoEmail(), "aparelho"))
                .andExpect(erro(404, "NAO_ENCONTRADO"));
    }

    @Test
    void refreshTrocaOParDeTokens() throws Exception {
        Conta conta = api.registrarComFazenda();

        JsonNode novoPar = api.json(renovar(conta.refreshToken(), conta.deviceId()).andExpect(status().isOk()));

        assertThat(novoPar.get("refreshToken").asString()).isNotEqualTo(conta.refreshToken());
        assertThat(novoPar.get("expiresIn").asInt()).isEqualTo(1800);
        api.getComToken("/usuarios/me", novoPar.get("accessToken").asString()).andExpect(status().isOk());
        assertThat(hashGuardado(conta)).doesNotContain(novoPar.get("refreshToken").asString());
    }

    @Test
    void refreshReutilizadoRevogaASessao() throws Exception {
        Conta conta = api.registrarComFazenda();
        JsonNode novoPar = api.json(renovar(conta.refreshToken(), conta.deviceId()).andExpect(status().isOk()));

        renovar(conta.refreshToken(), conta.deviceId()).andExpect(erro(401, "SESSAO_ENCERRADA"));

        renovar(novoPar.get("refreshToken").asString(), conta.deviceId()).andExpect(erro(401, "SESSAO_ENCERRADA"));
        api.getComToken("/usuarios/me", novoPar.get("accessToken").asString())
                .andExpect(erro(401, "TOKEN_INVALIDO"));
        assertThat(jdbc.queryForObject("select count(*) from sessoes where usuario_id = ? and revoked_at is not null",
                Integer.class, conta.usuarioId())).isEqualTo(1);
    }

    @Test
    void refreshVindoDeOutroAparelhoRevogaASessao() throws Exception {
        Conta conta = api.registrarComFazenda();

        renovar(conta.refreshToken(), "aparelho-de-outra-pessoa").andExpect(erro(401, "SESSAO_ENCERRADA"));

        renovar(conta.refreshToken(), conta.deviceId()).andExpect(erro(401, "SESSAO_ENCERRADA"));
    }

    @Test
    void refreshVencidoEncerraASessao() throws Exception {
        Conta conta = api.registrarComFazenda();
        jdbc.update("update sessoes set expires_at = now() - interval '1 day' where usuario_id = ?",
                conta.usuarioId());

        renovar(conta.refreshToken(), conta.deviceId()).andExpect(erro(401, "SESSAO_ENCERRADA"));
    }

    @Test
    void refreshDesconhecidoEncerraASessao() throws Exception {
        renovar("um-refresh-que-nunca-existiu", "aparelho").andExpect(erro(401, "SESSAO_ENCERRADA"));
    }

    @Test
    void logoutFazRefreshEAccessDaSessaoPararemDeFuncionar() throws Exception {
        Conta conta = api.registrarComFazenda();
        api.get("/usuarios/me", conta).andExpect(status().isOk());

        api.post("/auth/logout", conta, Map.of("refreshToken", conta.refreshToken()))
                .andExpect(status().isNoContent());

        renovar(conta.refreshToken(), conta.deviceId()).andExpect(erro(401, "SESSAO_ENCERRADA"));
        api.get("/usuarios/me", conta).andExpect(erro(401, "TOKEN_INVALIDO"));
    }

    @Test
    void logoutNaoEncerraSessaoDeOutroUsuario() throws Exception {
        Conta vitima = api.registrarComFazenda();
        Conta outro = api.registrarComFazenda();

        api.post("/auth/logout", outro, Map.of("refreshToken", vitima.refreshToken()))
                .andExpect(status().isNoContent());

        api.get("/usuarios/me", vitima).andExpect(status().isOk());
        renovar(vitima.refreshToken(), vitima.deviceId()).andExpect(status().isOk());
    }

    @Test
    void rotasProtegidasExigemAccessToken() throws Exception {
        api.getAnonimo("/usuarios/me").andExpect(erro(401, "TOKEN_INVALIDO"));
        api.getComToken("/usuarios/me", "isto-nao-e-um-jwt").andExpect(erro(401, "TOKEN_INVALIDO"));
        api.post("/auth/logout", Map.of("refreshToken", "qualquer")).andExpect(erro(401, "TOKEN_INVALIDO"));
    }

    @Test
    void refreshTokenFicaNoBancoApenasComoHash() throws Exception {
        Conta conta = api.registrarComFazenda();

        assertThat(hashGuardado(conta)).hasSize(64).isNotEqualTo(conta.refreshToken());
    }

    private org.springframework.test.web.servlet.ResultActions renovar(String refreshToken, String deviceId)
            throws Exception {
        return api.post("/auth/refresh", Map.of("refreshToken", refreshToken, "deviceId", deviceId));
    }

    private String hashGuardado(Conta conta) {
        return jdbc.queryForObject("select refresh_hash from sessoes where usuario_id = ?", String.class,
                conta.usuarioId());
    }

    private static Map<String, Object> corpoDeLogin(String email, String deviceId) {
        return Map.of("credencial", Map.of("tipo", "dev", "email", email), "deviceId", deviceId);
    }
}
