package com.minhafazenda.controller;

import static com.minhafazenda.apoio.ApiDeTeste.corpoDeRegistro;
import static com.minhafazenda.apoio.ApiDeTeste.novoEmail;
import static com.minhafazenda.apoio.Respostas.erro;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.minhafazenda.apoio.ApiDeTeste;
import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.Respostas;

import tools.jackson.databind.JsonNode;

class RegistroIntegrationTest extends IntegracaoTest {

    @Test
    void registroSemConviteCriaUsuarioFazendaEMembroOwner() throws Exception {
        String email = novoEmail();

        JsonNode resposta = api.json(api.post("/auth/registro",
                corpoDeRegistro("  Arthur  ", email, "Sítio Boa Vista", null)).andExpect(status().isCreated()));

        assertThat(resposta.get("accessToken").asString()).isNotBlank();
        assertThat(resposta.get("refreshToken").asString()).isNotBlank();
        assertThat(resposta.get("expiresIn").asInt()).isEqualTo(1800);
        assertThat(resposta.at("/usuario/nome").asString()).isEqualTo("Arthur");
        assertThat(resposta.get("usuario").has("avatarUrl")).isTrue();
        assertThat(resposta.at("/usuario/avatarUrl").isNull()).isTrue();

        UUID usuarioId = ApiDeTeste.uuid(resposta.at("/usuario/id"));
        UUID fazendaId = ApiDeTeste.uuid(resposta.get("fazendaId"));
        assertThat(jdbc.queryForObject("select email from usuarios where id = ?", String.class, usuarioId))
                .isEqualTo(email);
        assertThat(jdbc.queryForObject("select nome from fazendas where id = ?", String.class, fazendaId))
                .isEqualTo("Sítio Boa Vista");
        assertThat(papelDe(usuarioId, fazendaId)).isEqualTo("owner");
    }

    @Test
    void registroComConviteEntraComoCollaboratorNaFazendaDoConvite() throws Exception {
        Conta owner = api.registrarComFazenda();

        Conta convidado = api.registrarComConvite(api.criarConvite(owner));

        assertThat(convidado.fazendaId()).isEqualTo(owner.fazendaId());
        assertThat(papelDe(convidado.usuarioId(), owner.fazendaId())).isEqualTo("collaborator");
    }

    @Test
    void registroRecusaNomeFazendaEConviteJuntos() throws Exception {
        api.post("/auth/registro", corpoDeRegistro("Arthur", novoEmail(), "Sítio", "um-token"))
                .andExpect(erro(400, "VALIDACAO"));
    }

    @Test
    void registroRecusaQuandoFaltamNomeFazendaEConvite() throws Exception {
        api.post("/auth/registro", corpoDeRegistro("Arthur", novoEmail(), null, null))
                .andExpect(erro(400, "VALIDACAO"));
    }

    @Test
    void registroComConviteInvalidoNaoCriaUsuario() throws Exception {
        String email = novoEmail();

        api.post("/auth/registro", corpoDeRegistro("Arthur", email, null, "token-que-nao-existe"))
                .andExpect(erro(410, "CONVITE_INVALIDO"));

        assertThat(jdbc.queryForObject("select count(*) from usuarios where email = ?", Integer.class, email))
                .isZero();
    }

    @Test
    void registroRecusaEmailJaCadastrado() throws Exception {
        Conta existente = api.registrarComFazenda();

        api.post("/auth/registro", corpoDeRegistro("Outra Pessoa", existente.email(), "Outra Fazenda", null))
                .andExpect(erro(400, "VALIDACAO"));
    }

    @Test
    void registroRecusaNomeForaDoTamanho() throws Exception {
        api.post("/auth/registro", corpoDeRegistro("A", novoEmail(), "Sítio", null))
                .andExpect(erro(400, "VALIDACAO"));
        api.post("/auth/registro", corpoDeRegistro("A".repeat(61), novoEmail(), "Sítio", null))
                .andExpect(erro(400, "VALIDACAO"));
    }

    @Test
    void registroRecusaTipoDeCredencialDesconhecido() throws Exception {
        Map<String, Object> corpo = corpoDeRegistro("Arthur", novoEmail(), "Sítio", null);
        corpo.put("credencial", Map.of("tipo", "google", "email", novoEmail()));

        api.post("/auth/registro", corpo).andExpect(erro(400, "VALIDACAO"));
    }

    private String papelDe(UUID usuarioId, UUID fazendaId) {
        return jdbc.queryForObject(
                "select papel from fazenda_membros where usuario_id = ? and fazenda_id = ? and removed_at is null",
                String.class, usuarioId, fazendaId);
    }
}
