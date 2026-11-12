package com.minhafazenda.controller;

import static com.minhafazenda.apoio.Respostas.erro;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.minhafazenda.apoio.ApiDeTeste;
import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.Respostas;
import com.minhafazenda.security.TokenSeguro;

import tools.jackson.databind.JsonNode;

class ConviteIntegrationTest extends IntegracaoTest {

    @Test
    void ownerGeraConviteComLinkTokenEValidadeDe48Horas() throws Exception {
        Conta owner = api.registrarComFazenda();

        JsonNode convite = api.json(api.post(rotaDeConvites(owner), owner, null).andExpect(status().isCreated()));

        String token = convite.get("token").asString();
        assertThat(convite.get("url").asString()).endsWith("/convite/" + token);
        assertThat(Duration.between(Instant.now(), Instant.parse(convite.get("expiraEm").asString())))
                .isBetween(Duration.ofHours(47), Duration.ofHours(48));
        Map<String, Object> linha = jdbc.queryForMap("select token_hash, usos, usos_max from convites where id = ?",
                ApiDeTeste.uuid(convite.get("id")));
        assertThat(linha).containsEntry("token_hash", TokenSeguro.hash(token))
                .containsEntry("usos", 0)
                .containsEntry("usos_max", 1);
    }

    @Test
    void collaboratorNaoGeraNemRevogaConvite() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);
        UUID conviteId = criarConviteEObterId(owner);

        api.post(rotaDeConvites(owner), collaborator, null).andExpect(erro(403, "SEM_PERMISSAO"));
        api.delete("/convites/" + conviteId, collaborator).andExpect(erro(403, "SEM_PERMISSAO"));
    }

    @Test
    void aceitarConviteEntraNaFazendaComoCollaborator() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta convidado = api.registrarComFazenda();

        aceitar(convidado, api.criarConvite(owner))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fazendaId").value(owner.fazendaId().toString()));

        assertThat(papelDe(convidado, owner)).isEqualTo("collaborator");
    }

    @Test
    void conviteJaUsadoEInvalidoParaOutraPessoa() throws Exception {
        Conta owner = api.registrarComFazenda();
        String token = api.criarConvite(owner);
        aceitar(api.registrarComFazenda(), token).andExpect(status().isOk());

        aceitar(api.registrarComFazenda(), token).andExpect(erro(410, "CONVITE_INVALIDO"));
    }

    @Test
    void conviteVencidoEInvalido() throws Exception {
        Conta owner = api.registrarComFazenda();
        String token = api.criarConvite(owner);
        jdbc.update("update convites set expira_em = now() - interval '1 minute' where token_hash = ?",
                TokenSeguro.hash(token));

        aceitar(api.registrarComFazenda(), token).andExpect(erro(410, "CONVITE_INVALIDO"));
    }

    @Test
    void conviteRevogadoEInvalido() throws Exception {
        Conta owner = api.registrarComFazenda();
        JsonNode convite = api.json(api.post(rotaDeConvites(owner), owner, null));

        api.delete("/convites/" + convite.get("id").asString(), owner).andExpect(status().isNoContent());

        aceitar(api.registrarComFazenda(), convite.get("token").asString())
                .andExpect(erro(410, "CONVITE_INVALIDO"));
    }

    @Test
    void tokenDesconhecidoEInvalido() throws Exception {
        aceitar(api.registrarComFazenda(), "token-que-nao-existe").andExpect(erro(410, "CONVITE_INVALIDO"));
    }

    @Test
    void quemJaEMembroRecebe200SemConsumirOConvite() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);
        String outroToken = api.criarConvite(owner);

        aceitar(collaborator, outroToken)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fazendaId").value(owner.fazendaId().toString()));

        assertThat(jdbc.queryForObject("select usos from convites where token_hash = ?", Integer.class,
                TokenSeguro.hash(outroToken))).isZero();
        aceitar(api.registrarComFazenda(), outroToken).andExpect(status().isOk());
    }

    @Test
    void repetirOAceiteDepoisDeEntrarContinuaRespondendo200() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta convidado = api.registrarComFazenda();
        String token = api.criarConvite(owner);
        aceitar(convidado, token).andExpect(status().isOk());

        aceitar(convidado, token).andExpect(status().isOk());
    }

    @Test
    void membroRemovidoQueAceitaNovoConviteVoltaComoCollaborator() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta membro = api.registrarCollaboratorNaFazendaDe(owner);
        String rotaDoMembro = "/fazendas/%s/membros/%s".formatted(owner.fazendaId(), membro.usuarioId());
        api.patch(rotaDoMembro, owner, Map.of("papel", "owner")).andExpect(status().isNoContent());
        api.delete(rotaDoMembro, owner).andExpect(status().isNoContent());

        aceitar(membro, api.criarConvite(owner)).andExpect(status().isOk());

        assertThat(papelDe(membro, owner)).isEqualTo("collaborator");
        assertThat(jdbc.queryForObject(
                "select count(*) from fazenda_membros where usuario_id = ? and fazenda_id = ? and removed_at is null",
                Integer.class, membro.usuarioId(), owner.fazendaId())).isEqualTo(1);
    }

    @Test
    void revogarConviteInexistenteRespondeNaoEncontrado() throws Exception {
        api.delete("/convites/" + UUID.randomUUID(), api.registrarComFazenda())
                .andExpect(erro(404, "NAO_ENCONTRADO"));
    }

    // Código de convite (6 caracteres, 24 h, uma pessoa)

    @Test
    void ownerGeraCodigoDeSeisCaracteresValidoPorUmDiaComMensagemParaCompartilhar() throws Exception {
        Conta owner = api.registrarComFazenda();
        Instant antes = Instant.now();

        JsonNode codigo = api.json(api.post(rotaDeConvites(owner) + "/codigo", owner, null)
                .andExpect(status().isCreated()));

        assertThat(codigo.get("codigo").asString()).matches("[ABCDEFGHJKMNPQRSTUVWXYZ23456789]{6}");
        assertThat(codigo.get("mensagem").asString()).isEqualTo("Entre na minha fazenda: " + codigo.get("codigo").asString());
        Instant expiraEm = Instant.parse(codigo.get("expiraEm").asString());
        assertThat(Duration.between(antes, expiraEm)).isBetween(Duration.ofHours(23).plusMinutes(59), Duration.ofHours(24).plusMinutes(1));
    }

    @Test
    void codigoDigitadoComMinusculasEspacoEHifenEntraComoCollaborator() throws Exception {
        Conta owner = api.registrarComFazenda();
        String codigo = gerarCodigo(owner);
        String digitado = " " + codigo.substring(0, 3).toLowerCase() + "-" + codigo.substring(3) + " ";

        Conta convidado = api.registrarComFazenda();
        aceitar(convidado, digitado)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fazendaId").value(owner.fazendaId().toString()));

        assertThat(papelDe(convidado, owner)).isEqualTo("collaborator");
    }

    @Test
    void codigoSoServeParaUmaPessoa() throws Exception {
        Conta owner = api.registrarComFazenda();
        String codigo = gerarCodigo(owner);
        aceitar(api.registrarComFazenda(), codigo).andExpect(status().isOk());

        aceitar(api.registrarComFazenda(), codigo).andExpect(erro(410, "CONVITE_INVALIDO"));
    }

    @Test
    void codigoVencidoDepoisDeUmDia() throws Exception {
        Conta owner = api.registrarComFazenda();
        String codigo = gerarCodigo(owner);
        jdbc.update("update convites set expira_em = now() - interval '1 minute' where codigo = ?", codigo);

        aceitar(api.registrarComFazenda(), codigo).andExpect(erro(410, "CONVITE_INVALIDO"));
        api.get(rotaDeConvites(owner) + "/codigo", owner).andExpect(erro(404, "NAO_ENCONTRADO"));
    }

    @Test
    void cadastroComCodigoEntraNaFazenda() throws Exception {
        Conta owner = api.registrarComFazenda();

        Conta novo = api.registrarComConvite(gerarCodigo(owner).toLowerCase());

        assertThat(novo.fazendaId()).isEqualTo(owner.fazendaId());
        assertThat(papelDe(novo, owner)).isEqualTo("collaborator");
    }

    @Test
    void gerarOutroCodigoNaoInvalidaOAnteriorEODonoVeOUltimo() throws Exception {
        Conta owner = api.registrarComFazenda();
        String primeiro = gerarCodigo(owner);
        String segundo = gerarCodigo(owner);

        api.get(rotaDeConvites(owner) + "/codigo", owner)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(segundo));
        aceitar(api.registrarComFazenda(), primeiro).andExpect(status().isOk());
        aceitar(api.registrarComFazenda(), segundo).andExpect(status().isOk());
    }

    @Test
    void collaboratorNaoGeraNemVeCodigo() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);

        api.post(rotaDeConvites(owner) + "/codigo", collaborator, null).andExpect(erro(403, "SEM_PERMISSAO"));
        api.get(rotaDeConvites(owner) + "/codigo", collaborator).andExpect(erro(403, "SEM_PERMISSAO"));
    }

    private String gerarCodigo(Conta owner) throws Exception {
        return api.json(api.post(rotaDeConvites(owner) + "/codigo", owner, null).andExpect(status().isCreated()))
                .get("codigo").asString();
    }

    private org.springframework.test.web.servlet.ResultActions aceitar(Conta conta, String token) throws Exception {
        return api.post("/convites/aceitar", conta, Map.of("token", token));
    }

    private UUID criarConviteEObterId(Conta owner) throws Exception {
        return ApiDeTeste.uuid(api.json(api.post(rotaDeConvites(owner), owner, null)).get("id"));
    }

    private static String rotaDeConvites(Conta owner) {
        return "/fazendas/" + owner.fazendaId() + "/convites";
    }

    private String papelDe(Conta membro, Conta donoDaFazenda) {
        return jdbc.queryForObject("select papel from fazenda_membros where usuario_id = ? and fazenda_id = ?",
                String.class, membro.usuarioId(), donoDaFazenda.fazendaId());
    }
}
