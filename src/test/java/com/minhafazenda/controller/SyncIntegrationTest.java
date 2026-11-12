package com.minhafazenda.controller;

import static com.minhafazenda.apoio.OpDeSync.delete;
import static com.minhafazenda.apoio.OpDeSync.patch;
import static com.minhafazenda.apoio.OpDeSync.put;
import static com.minhafazenda.apoio.OpDeSync.putAnimal;
import static com.minhafazenda.apoio.Respostas.erro;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.OpDeSync;
import com.minhafazenda.apoio.Respostas;
import com.minhafazenda.apoio.ValidadorDeNomeProibido;
import com.minhafazenda.infra.SchemaCache;

class SyncIntegrationTest extends IntegracaoTest {

    private static final String ANIMAIS = "animais";

    @Autowired
    private SchemaCache schemaCache;

    @Test
    void putCriaORegistroComAutorEDataDoServidor() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID id = UUID.randomUUID();
        Map<String, Object> data = Map.of(
                "nome", "Mimosa",
                "fazenda_id", conta.fazendaId().toString(),
                "id", UUID.randomUUID().toString(),
                "created_by", UUID.randomUUID().toString(),
                "server_updated_at", "2000-01-01T00:00:00Z",
                "created_at", "2026-11-11T12:00:00Z",
                "coluna_que_nao_existe", "ignorada");

        api.sync(conta, List.of(putAnimal(id, data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicadas").value(1))
                .andExpect(jsonPath("$.rejeitadas").isEmpty());

        Map<String, Object> animal = animal(id);
        assertThat(animal).containsEntry("nome", "Mimosa")
                .containsEntry("fazenda_id", conta.fazendaId())
                .containsEntry("created_by", conta.usuarioId());
        assertThat(animal.get("created_at")).isNotNull();
        assertThat(jdbc.queryForObject("select server_updated_at > now() - interval '1 minute' from animais where id = ?",
                Boolean.class, id)).isTrue();
    }

    @Test
    void patchAlteraSoAsColunasEnviadasEDeleteMarcaDeletedAt() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID id = criarAnimal(conta, "Mimosa");

        api.sync(conta, List.of(patch(ANIMAIS, id, Map.of("updated_at", "2026-11-11T13:00:00Z"))))
                .andExpect(jsonPath("$.aplicadas").value(1));
        assertThat(animal(id)).containsEntry("nome", "Mimosa");
        assertThat(animal(id).get("updated_at")).isNotNull();

        api.sync(conta, List.of(delete(ANIMAIS, id))).andExpect(jsonPath("$.aplicadas").value(1));
        assertThat(animal(id).get("deleted_at")).isNotNull();
    }

    @Test
    void usuarioDaFazendaANaoCriaAlteraNemApagaRegistroDaFazendaB() throws Exception {
        Conta a = api.registrarComFazenda();
        Conta b = api.registrarComFazenda();
        UUID animalDeB = criarAnimal(b, "Estrela");
        UUID novo = UUID.randomUUID();

        ResultActions resposta = api.sync(a, List.of(
                putAnimal(novo, Map.of("nome", "Intruso", "fazenda_id", b.fazendaId().toString())),
                patch(ANIMAIS, animalDeB, Map.of("nome", "Alterado")),
                putAnimal(animalDeB, Map.of("nome", "Sobrescrito", "fazenda_id", a.fazendaId().toString())),
                delete(ANIMAIS, animalDeB)));

        resposta.andExpect(status().isOk()).andExpect(jsonPath("$.aplicadas").value(0));
        assertThat(api.codigosRejeitados(resposta))
                .containsExactly("SEM_PERMISSAO", "SEM_PERMISSAO", "SEM_PERMISSAO", "SEM_PERMISSAO");

        assertThat(contarAnimais(novo)).isZero();
        assertThat(animal(animalDeB)).containsEntry("nome", "Estrela")
                .containsEntry("fazenda_id", b.fazendaId())
                .containsEntry("deleted_at", null);
        assertThat(jdbc.queryForObject("select count(*) from sync_rejeicoes where usuario_id = ?", Integer.class,
                a.usuarioId())).isEqualTo(4);
    }

    @Test
    void ninguemMudaOFazendaIdDeUmRegistro() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID outraFazenda = UUID.fromString(api.json(api.post("/fazendas", conta, Map.of("nome", "Segunda")))
                .get("id").asString());
        UUID id = criarAnimal(conta, "Mimosa");

        api.sync(conta, List.of(
                patch(ANIMAIS, id, Map.of("fazenda_id", outraFazenda.toString(), "nome", "Via PATCH")),
                putAnimal(id, Map.of("fazenda_id", outraFazenda.toString(), "nome", "Via PUT"))))
                .andExpect(jsonPath("$.aplicadas").value(2));

        assertThat(animal(id)).containsEntry("nome", "Via PUT").containsEntry("fazenda_id", conta.fazendaId());
    }

    @Test
    void membroRemovidoPerdeAEscritaNaHora() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);
        UUID id = criarAnimal(collaborator, "Do colaborador");
        api.delete("/fazendas/%s/membros/%s".formatted(owner.fazendaId(), collaborator.usuarioId()), owner)
                .andExpect(status().isNoContent());

        api.sync(collaborator, List.of(patch(ANIMAIS, id, Map.of("nome", "Depois de sair"))))
                .andExpect(jsonPath("$.aplicadas").value(0))
                .andExpect(jsonPath("$.rejeitadas[0].codigo").value("SEM_PERMISSAO"));

        assertThat(animal(id)).containsEntry("nome", "Do colaborador");
    }

    @Test
    void collaboratorApagaRegistrosEnquantoAFlagPermite() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);
        UUID id = criarAnimal(owner, "Mimosa");

        api.sync(collaborator, List.of(delete(ANIMAIS, id))).andExpect(jsonPath("$.aplicadas").value(1));

        assertThat(animal(id).get("deleted_at")).isNotNull();
    }

    @Test
    void colunaNovaCriadaPorChangesetEAceitaSemMudarCodigo() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID id = UUID.randomUUID();
        Map<String, Object> data = Map.of("nome", "Pesada", "fazenda_id", conta.fazendaId().toString(), "peso", 432.5);
        jdbc.execute("alter table animais add column peso numeric(10,2)");
        try {
            // Equivale a reiniciar a API depois do changeset: o cache é lido na subida.
            schemaCache.recarregar();

            api.sync(conta, List.of(putAnimal(id, data))).andExpect(jsonPath("$.aplicadas").value(1));

            assertThat(jdbc.queryForObject("select peso from animais where id = ?", BigDecimal.class, id))
                    .isEqualByComparingTo("432.50");
        } finally {
            jdbc.execute("alter table animais drop column peso");
            schemaCache.recarregar();
        }
    }

    @Test
    void sqlInjectionEmTabelaColunaOuValorNaoTemEfeito() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID id = UUID.randomUUID();
        String valorMalicioso = "'); drop table animais; --";

        ResultActions resposta = api.sync(conta, List.of(
                put("animais; drop table usuarios; --", UUID.randomUUID(), Map.of("nome", "x")),
                put("animais\" where true; --", UUID.randomUUID(), Map.of("nome", "x")),
                putAnimal(id, Map.of(
                        "fazenda_id", conta.fazendaId().toString(),
                        "nome", valorMalicioso,
                        "nome\" = 'invadido', \"fazenda_id", "x",
                        "nome; drop table fazendas", "x")),
                putAnimal("1'; drop table animais; --", Map.of("nome", "x"))));

        resposta.andExpect(status().isOk()).andExpect(jsonPath("$.aplicadas").value(1));
        assertThat(api.codigosRejeitados(resposta)).containsExactly("VALIDACAO", "VALIDACAO", "VALIDACAO");

        assertThat(animal(id)).containsEntry("nome", valorMalicioso).containsEntry("fazenda_id", conta.fazendaId());
        assertThat(jdbc.queryForObject("""
                select count(*) from information_schema.tables
                where table_schema = 'public' and table_name in ('animais', 'usuarios', 'fazendas')
                """, Integer.class)).isEqualTo(3);
    }

    @Test
    void tabelasForaDaWhitelistNuncaSaoGravadas() throws Exception {
        Conta conta = api.registrarComFazenda();
        List<String> protegidas = List.of("usuarios", "sessoes", "fazendas", "fazenda_membros", "convites",
                "sync_rejeicoes", "auditoria_fazenda");
        List<Map<String, Object>> ops = protegidas.stream()
                .map(tabela -> patch(tabela, conta.usuarioId(), Map.of("nome", "Invadido", "papel", "owner")))
                .toList();

        api.sync(conta, ops)
                .andExpect(jsonPath("$.aplicadas").value(0))
                .andExpect(jsonPath("$.rejeitadas.length()").value(protegidas.size()));

        assertThat(jdbc.queryForObject("select nome from usuarios where id = ?", String.class, conta.usuarioId()))
                .isEqualTo("Pessoa de Teste");
    }

    @Test
    void reenviarOMesmoLoteDeixaOBancoIgual() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID mantido = UUID.randomUUID();
        UUID apagado = UUID.randomUUID();
        String fazendaId = conta.fazendaId().toString();
        List<Map<String, Object>> lote = List.of(
                putAnimal(mantido, Map.of("nome", "Mimosa", "fazenda_id", fazendaId, "created_at", "2026-11-11T12:00:00Z")),
                patch(ANIMAIS, mantido, Map.of("nome", "Mimosa II")),
                putAnimal(apagado, Map.of("nome", "Vendida", "fazenda_id", fazendaId)),
                delete(ANIMAIS, apagado));

        api.sync(conta, lote).andExpect(jsonPath("$.aplicadas").value(4));
        List<Map<String, Object>> depoisDoPrimeiroEnvio = animaisDaFazenda(conta);
        ResultActions segundoEnvio = api.sync(conta, lote);

        segundoEnvio.andExpect(status().isOk()).andExpect(jsonPath("$.aplicadas").value(4));
        assertThat(animaisDaFazenda(conta)).hasSize(2).isEqualTo(depoisDoPrimeiroEnvio);
    }

    @Test
    void valorInvalidoRejeitaSoAquelaOperacaoEOLoteContinua() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID primeiro = UUID.randomUUID();
        UUID segundo = UUID.randomUUID();
        String fazendaId = conta.fazendaId().toString();

        api.sync(conta, List.of(
                putAnimal(primeiro, Map.of("nome", "Antes", "fazenda_id", fazendaId)),
                patch(ANIMAIS, primeiro, Map.of("created_at", "isto-nao-e-uma-data")),
                putAnimal(segundo, Map.of("nome", "Depois", "fazenda_id", fazendaId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicadas").value(2))
                .andExpect(jsonPath("$.rejeitadas[0].table").value(ANIMAIS))
                .andExpect(jsonPath("$.rejeitadas[0].id").value(primeiro.toString()))
                .andExpect(jsonPath("$.rejeitadas[0].codigo").value("VALIDACAO"))
                .andExpect(jsonPath("$.rejeitadas[0].mensagem").isNotEmpty());

        assertThat(contarAnimais(primeiro)).isEqualTo(1);
        assertThat(contarAnimais(segundo)).isEqualTo(1);
        Map<String, Object> rejeicao = jdbc.queryForMap(
                "select tabela, registro_id, op, dados::text as dados, motivo from sync_rejeicoes where usuario_id = ?",
                conta.usuarioId());
        assertThat(rejeicao).containsEntry("tabela", ANIMAIS)
                .containsEntry("registro_id", primeiro.toString())
                .containsEntry("op", "PATCH");
        assertThat((String) rejeicao.get("dados")).contains("isto-nao-e-uma-data");
        assertThat((String) rejeicao.get("motivo")).startsWith("VALIDACAO");
    }

    @Test
    void patchDeRegistroInexistenteERejeitadoEDeleteEIdempotente() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.sync(conta, List.of(
                patch(ANIMAIS, UUID.randomUUID(), Map.of("nome", "Fantasma")),
                delete(ANIMAIS, UUID.randomUUID())))
                .andExpect(jsonPath("$.aplicadas").value(1))
                .andExpect(jsonPath("$.rejeitadas[0].codigo").value("NAO_ENCONTRADO"));
    }

    @Test
    void putNovoSemFazendaIdERejeitado() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.sync(conta, List.of(putAnimal(UUID.randomUUID(), Map.of("nome", "Sem fazenda"))))
                .andExpect(jsonPath("$.aplicadas").value(0))
                .andExpect(jsonPath("$.rejeitadas[0].codigo").value("VALIDACAO"));
    }

    @Test
    void validadorDaTabelaRejeitaAOperacao() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID id = UUID.randomUUID();

        api.sync(conta, List.of(putAnimal(id,
                Map.of("nome", ValidadorDeNomeProibido.NOME_PROIBIDO, "fazenda_id", conta.fazendaId().toString()))))
                .andExpect(jsonPath("$.aplicadas").value(0))
                .andExpect(jsonPath("$.rejeitadas[0].codigo").value("VALIDACAO"));

        assertThat(contarAnimais(id)).isZero();
    }

    @Test
    void appDesatualizadoRecebe426ENadaEAplicado() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID id = UUID.randomUUID();

        api.sync(conta, 4, List.of(putAnimal(id, Map.of("nome", "X", "fazenda_id", conta.fazendaId().toString()))))
                .andExpect(erro(426, "APP_DESATUALIZADO"));

        assertThat(contarAnimais(id)).isZero();
    }

    @Test
    void syncExigeAccessToken() throws Exception {
        api.post("/sync/upload", Map.of("schemaVersion", 7, "ops", List.of())).andExpect(erro(401, "TOKEN_INVALIDO"));
    }

    @Test
    void operacaoDesconhecidaEErroDeValidacaoDoLote() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.sync(conta, List.of(Map.of("op", "TRUNCATE", "table", ANIMAIS, "id", UUID.randomUUID().toString())))
                .andExpect(erro(400, "VALIDACAO"));
    }

    private UUID criarAnimal(Conta conta, String nome) throws Exception {
        UUID id = UUID.randomUUID();
        api.sync(conta, List.of(putAnimal(id, Map.of("nome", nome, "fazenda_id", conta.fazendaId().toString()))))
                .andExpect(jsonPath("$.aplicadas").value(1));
        return id;
    }

    private Map<String, Object> animal(UUID id) {
        return jdbc.queryForMap("select * from animais where id = ?", id);
    }

    private int contarAnimais(UUID id) {
        return jdbc.queryForObject("select count(*) from animais where id = ?", Integer.class, id);
    }

    /** Estado dos registros sem server_updated_at, que muda a cada escrita por definição. */
    private List<Map<String, Object>> animaisDaFazenda(Conta conta) {
        return jdbc.queryForList("""
                select id, fazenda_id, nome, created_by, created_at, updated_at, deleted_at
                from animais where fazenda_id = ? order by id
                """, conta.fazendaId());
    }
}
