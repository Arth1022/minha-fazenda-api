package com.minhafazenda.controller;

import static com.minhafazenda.apoio.OpDeSync.delete;
import static com.minhafazenda.apoio.OpDeSync.patch;
import static com.minhafazenda.apoio.OpDeSync.putAnimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;

import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.OpDeSync;

@TestPropertySource(properties = "permissoes.collaborator-pode-apagar=false")
class SyncSemPermissaoDeApagarIntegrationTest extends IntegracaoTest {

    private static final String ANIMAIS = "animais";

    @Test
    void collaboratorNaoApagaNemPorDeleteNemGravandoDeletedAt() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);
        UUID id = criarAnimal(owner);

        ResultActions resposta = api.sync(collaborator, List.of(
                delete(ANIMAIS, id),
                patch(ANIMAIS, id, Map.of("deleted_at", "2026-11-11T12:00:00Z"))));

        resposta.andExpect(jsonPath("$.aplicadas").value(0));
        assertThat(api.codigosRejeitados(resposta)).containsExactly("SEM_PERMISSAO", "SEM_PERMISSAO");

        assertThat(deletedAt(id)).isNull();
    }

    @Test
    void collaboratorContinuaEditandoEOwnerContinuaApagando() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);
        UUID id = criarAnimal(owner);

        api.sync(collaborator, List.of(patch(ANIMAIS, id, Map.of("nome", "Editado"))))
                .andExpect(jsonPath("$.aplicadas").value(1));
        api.sync(owner, List.of(delete(ANIMAIS, id))).andExpect(jsonPath("$.aplicadas").value(1));

        assertThat(deletedAt(id)).isNotNull();
    }

    private UUID criarAnimal(Conta conta) throws Exception {
        UUID id = UUID.randomUUID();
        api.sync(conta, List.of(putAnimal(id, Map.of("nome", "Mimosa", "fazenda_id", conta.fazendaId().toString()))))
                .andExpect(jsonPath("$.aplicadas").value(1));
        return id;
    }

    private Object deletedAt(UUID id) {
        return jdbc.queryForObject("select deleted_at from animais where id = ?", Object.class, id);
    }
}
