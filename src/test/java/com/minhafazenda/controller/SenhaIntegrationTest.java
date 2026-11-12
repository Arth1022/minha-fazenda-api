package com.minhafazenda.controller;

import static com.minhafazenda.apoio.ApiDeTeste.corpoDeRegistro;
import static com.minhafazenda.apoio.ApiDeTeste.novoEmail;
import static com.minhafazenda.apoio.Respostas.erro;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.minhafazenda.apoio.IntegracaoTest;

import tools.jackson.databind.JsonNode;

class SenhaIntegrationTest extends IntegracaoTest {

    private static final String SENHA = "uma senha boa";

    @Test
    void registroGuardaSoOHashELoginComASenhaCertaDevolveTokens() throws Exception {
        String email = novoEmail();

        api.post("/auth/registro", registroComSenha(email, SENHA)).andExpect(status().isCreated());

        String hash = jdbc.queryForObject("select credencial_hash from usuarios where email = ?", String.class, email);
        assertThat(hash).startsWith("{bcrypt}").doesNotContain(SENHA);

        JsonNode login = api.json(api.post("/auth/login", loginComSenha(email, SENHA)).andExpect(status().isOk()));
        assertThat(login.get("accessToken").asString()).isNotBlank();
        assertThat(login.get("refreshToken").asString()).isNotBlank();
    }

    @Test
    void loginIgnoraMaiusculasNoEmail() throws Exception {
        String email = novoEmail();
        api.post("/auth/registro", registroComSenha(email, SENHA)).andExpect(status().isCreated());

        api.post("/auth/login", loginComSenha(email.toUpperCase(), SENHA)).andExpect(status().isOk());
    }

    @Test
    void senhaErradaEEmailInexistenteRecebemAMesmaResposta() throws Exception {
        String email = novoEmail();
        api.post("/auth/registro", registroComSenha(email, SENHA)).andExpect(status().isCreated());

        api.post("/auth/login", loginComSenha(email, "outra senha qualquer"))
                .andExpect(erro(401, "CREDENCIAL_INVALIDA"));
        api.post("/auth/login", loginComSenha(novoEmail(), SENHA))
                .andExpect(erro(401, "CREDENCIAL_INVALIDA"));
        api.post("/auth/login", loginComSenha(email, null))
                .andExpect(erro(401, "CREDENCIAL_INVALIDA"));
    }

    @Test
    void contaSemSenhaNaoEntraPorSenha() throws Exception {
        String email = novoEmail();
        api.post("/auth/registro", corpoDeRegistro("Arthur", email, "Sítio", null)).andExpect(status().isCreated());

        api.post("/auth/login", loginComSenha(email, SENHA)).andExpect(erro(401, "CREDENCIAL_INVALIDA"));
    }

    @Test
    void registroRecusaSenhaCurtaOuLongaDemaisSemCriarUsuario() throws Exception {
        String email = novoEmail();

        api.post("/auth/registro", registroComSenha(email, "curta")).andExpect(erro(400, "VALIDACAO"));
        api.post("/auth/registro", registroComSenha(email, "ç".repeat(37))).andExpect(erro(400, "VALIDACAO"));
        api.post("/auth/registro", registroComSenha(email, null)).andExpect(erro(400, "VALIDACAO"));

        assertThat(jdbc.queryForObject("select count(*) from usuarios where email = ?", Integer.class, email))
                .isZero();
    }

    @Test
    void registroRecusaEmailJaCadastrado() throws Exception {
        String email = novoEmail();
        api.post("/auth/registro", registroComSenha(email, SENHA)).andExpect(status().isCreated());

        api.post("/auth/registro", registroComSenha(email, SENHA)).andExpect(erro(400, "VALIDACAO"));
    }

    private static Map<String, Object> registroComSenha(String email, String senha) {
        Map<String, Object> corpo = corpoDeRegistro("Arthur", email, "Sítio Boa Vista", null);
        corpo.put("credencial", credencial(email, senha));
        return corpo;
    }

    private static Map<String, Object> loginComSenha(String email, String senha) {
        return Map.of("credencial", credencial(email, senha), "deviceId", UUID.randomUUID().toString());
    }

    private static Map<String, Object> credencial(String email, String senha) {
        Map<String, Object> credencial = new HashMap<>();
        credencial.put("tipo", "senha");
        credencial.put("email", email);
        credencial.put("senha", senha);
        return credencial;
    }
}
