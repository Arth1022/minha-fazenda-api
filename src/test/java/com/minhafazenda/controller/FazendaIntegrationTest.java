package com.minhafazenda.controller;

import static com.minhafazenda.apoio.Respostas.erro;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.minhafazenda.apoio.ApiDeTeste;
import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.Respostas;

import tools.jackson.databind.JsonNode;

class FazendaIntegrationTest extends IntegracaoTest {

    @Test
    void criarFazendaTornaOUsuarioOwner() throws Exception {
        Conta conta = api.registrarComFazenda();

        JsonNode fazenda = api.json(api.post("/fazendas", conta, Map.of("nome", "  Chácara Nova  "))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Chácara Nova")));

        assertThat(papelDe(conta, ApiDeTeste.uuid(fazenda.get("id")))).isEqualTo("owner");
    }

    @Test
    void criarFazendaAceitaIdGeradoPeloAppERepetirEIdempotente() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID idDoApp = UUID.randomUUID();
        Map<String, Object> corpo = Map.of("id", idDoApp.toString(), "nome", "Fazenda Local");

        api.post("/fazendas", conta, corpo)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(idDoApp.toString()));
        api.post("/fazendas", conta, corpo).andExpect(status().isCreated());

        assertThat(jdbc.queryForObject("select count(*) from fazenda_membros where fazenda_id = ?", Integer.class,
                idDoApp)).isEqualTo(1);
    }

    @Test
    void criarFazendaRecusaIdQueJaPertenceAOutraPessoa() throws Exception {
        Conta dona = api.registrarComFazenda();
        Conta outra = api.registrarComFazenda();

        api.post("/fazendas", outra, Map.of("id", dona.fazendaId().toString(), "nome", "Tentativa"))
                .andExpect(erro(400, "VALIDACAO"));

        assertThat(jdbc.queryForObject("select nome from fazendas where id = ?", String.class, dona.fazendaId()))
                .isEqualTo("Fazenda de Teste");
    }

    @Test
    void criarFazendaExigeNome() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.post("/fazendas", conta, Map.of("nome", "  ")).andExpect(erro(400, "VALIDACAO"));
    }

    @Test
    void ownerRenomeiaAFazenda() throws Exception {
        Conta owner = api.registrarComFazenda();

        api.patch("/fazendas/" + owner.fazendaId(), owner, Map.of("nome", "Sítio Renomeado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(owner.fazendaId().toString()))
                .andExpect(jsonPath("$.nome").value("Sítio Renomeado"));
    }

    @Test
    void collaboratorEQuemNaoEMembroNaoRenomeiam() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);
        Conta estranho = api.registrarComFazenda();
        String rota = "/fazendas/" + owner.fazendaId();

        api.patch(rota, collaborator, Map.of("nome", "Invadida")).andExpect(erro(403, "SEM_PERMISSAO"));
        api.patch(rota, estranho, Map.of("nome", "Invadida")).andExpect(erro(403, "SEM_PERMISSAO"));
    }

    @Test
    void idMalFormadoNaRotaEErroDeValidacao() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.patch("/fazendas/nao-e-uuid", conta, Map.of("nome", "X")).andExpect(erro(400, "VALIDACAO"));
    }

    private String papelDe(Conta conta, UUID fazendaId) {
        return jdbc.queryForObject("select papel from fazenda_membros where usuario_id = ? and fazenda_id = ?",
                String.class, conta.usuarioId(), fazendaId);
    }
}
