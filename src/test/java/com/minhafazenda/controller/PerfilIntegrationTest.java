package com.minhafazenda.controller;

import static com.minhafazenda.apoio.Respostas.erro;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.Respostas;

class PerfilIntegrationTest extends IntegracaoTest {

    @Test
    void devolveOPerfilDoUsuarioLogado() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.get("/usuarios/me", conta)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(conta.usuarioId().toString()))
                .andExpect(jsonPath("$.nome").value("Pessoa de Teste"))
                .andExpect(jsonPath("$.avatarUrl").value((Object) null))
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    void alteraONomeRemovendoEspacosDasPontas() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.patch("/usuarios/me", conta, Map.of("nome", "   Álvaro Teixeira  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Álvaro Teixeira"));

        api.get("/usuarios/me", conta).andExpect(jsonPath("$.nome").value("Álvaro Teixeira"));
    }

    @Test
    void recusaNomeComMenosDeDoisOuMaisDeSessentaCaracteres() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.patch("/usuarios/me", conta, Map.of("nome", " A ")).andExpect(erro(400, "VALIDACAO"));
        api.patch("/usuarios/me", conta, Map.of("nome", "A".repeat(61))).andExpect(erro(400, "VALIDACAO"));
        api.patch("/usuarios/me", conta, Map.of("nome", "   ")).andExpect(erro(400, "VALIDACAO"));
        api.patch("/usuarios/me", conta, Map.of()).andExpect(erro(400, "VALIDACAO"));
    }

    @Test
    void aceitaNomeNosLimites() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.patch("/usuarios/me", conta, Map.of("nome", "Jó")).andExpect(status().isOk());
        api.patch("/usuarios/me", conta, Map.of("nome", "A".repeat(60))).andExpect(status().isOk());
    }
}
