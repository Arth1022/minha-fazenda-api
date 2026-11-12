package com.minhafazenda.controller;

import static com.minhafazenda.apoio.Respostas.erro;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.Respostas;

class MembroIntegrationTest extends IntegracaoTest {

    @Test
    void collaboratorNaoMudaPapelNemRemoveMembro() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);

        api.patch(rotaDoMembro(owner, collaborator), collaborator, Map.of("papel", "owner"))
                .andExpect(erro(403, "SEM_PERMISSAO"));
        api.delete(rotaDoMembro(owner, owner), collaborator).andExpect(erro(403, "SEM_PERMISSAO"));

        assertThat(papelDe(collaborator, owner)).isEqualTo("collaborator");
    }

    @Test
    void ownerPromoveERebaixaOutroMembro() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);

        api.patch(rotaDoMembro(owner, collaborator), owner, Map.of("papel", "owner"))
                .andExpect(status().isNoContent());
        assertThat(papelDe(collaborator, owner)).isEqualTo("owner");

        api.patch(rotaDoMembro(owner, collaborator), owner, Map.of("papel", "collaborator"))
                .andExpect(status().isNoContent());
        assertThat(papelDe(collaborator, owner)).isEqualTo("collaborator");
    }

    @Test
    void papelDesconhecidoEErroDeValidacao() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);

        api.patch(rotaDoMembro(owner, collaborator), owner, Map.of("papel", "admin"))
                .andExpect(erro(400, "VALIDACAO"));
    }

    @Test
    void ultimoOwnerNaoSaiNemSeRebaixa() throws Exception {
        Conta owner = api.registrarComFazenda();
        api.registrarCollaboratorNaFazendaDe(owner);

        api.delete(rotaDeSaida(owner), owner).andExpect(erro(409, "ULTIMO_OWNER"));
        api.patch(rotaDoMembro(owner, owner), owner, Map.of("papel", "collaborator"))
                .andExpect(erro(409, "ULTIMO_OWNER"));
        api.delete(rotaDoMembro(owner, owner), owner).andExpect(erro(409, "ULTIMO_OWNER"));

        assertThat(papelDe(owner, owner)).isEqualTo("owner");
    }

    @Test
    void ownerSaiDepoisDePromoverOutraPessoa() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta sucessor = api.registrarCollaboratorNaFazendaDe(owner);
        api.patch(rotaDoMembro(owner, sucessor), owner, Map.of("papel", "owner")).andExpect(status().isNoContent());

        api.delete(rotaDeSaida(owner), owner).andExpect(status().isNoContent());

        api.patch("/fazendas/" + owner.fazendaId(), owner, Map.of("nome", "X")).andExpect(erro(403, "SEM_PERMISSAO"));
        api.patch("/fazendas/" + owner.fazendaId(), sucessor, Map.of("nome", "Nova Gestão"))
                .andExpect(status().isOk());
    }

    @Test
    void collaboratorSaiDaFazenda() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);

        api.delete(rotaDeSaida(owner), collaborator).andExpect(status().isNoContent());

        assertThat(removidoEm(collaborator, owner)).isNotNull();
    }

    @Test
    void removerPreencheRemovedAtSemApagarALinha() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);

        api.delete(rotaDoMembro(owner, collaborator), owner).andExpect(status().isNoContent());

        assertThat(removidoEm(collaborator, owner)).isNotNull();
        api.delete(rotaDoMembro(owner, collaborator), owner).andExpect(erro(404, "NAO_ENCONTRADO"));
    }

    @Test
    void quemNaoEMembroNaoGerenciaAFazenda() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta estranho = api.registrarComFazenda();

        api.delete(rotaDoMembro(owner, owner), estranho).andExpect(erro(403, "SEM_PERMISSAO"));
        api.delete(rotaDeSaida(owner), estranho).andExpect(erro(403, "SEM_PERMISSAO"));
    }

    @Test
    void mudancasDePessoasFicamNaAuditoria() throws Exception {
        Conta owner = api.registrarComFazenda();
        Conta collaborator = api.registrarCollaboratorNaFazendaDe(owner);
        api.patch(rotaDoMembro(owner, collaborator), owner, Map.of("papel", "owner"));
        api.delete(rotaDoMembro(owner, collaborator), owner);

        List<String> acoes = jdbc.queryForList(
                "select acao from auditoria_fazenda where fazenda_id = ? order by created_at", String.class,
                owner.fazendaId());

        assertThat(acoes).containsExactly("CONVITE_CRIADO", "CONVITE_ACEITO", "PAPEL_ALTERADO", "MEMBRO_REMOVIDO");
        assertThat(jdbc.queryForObject("""
                select count(*) from auditoria_fazenda
                where fazenda_id = ? and acao = 'MEMBRO_REMOVIDO' and ator_id = ? and alvo_id = ?
                """, Integer.class, owner.fazendaId(), owner.usuarioId(), collaborator.usuarioId())).isEqualTo(1);
    }

    private static String rotaDoMembro(Conta donoDaFazenda, Conta membro) {
        return "/fazendas/%s/membros/%s".formatted(donoDaFazenda.fazendaId(), membro.usuarioId());
    }

    private static String rotaDeSaida(Conta donoDaFazenda) {
        return "/fazendas/%s/membros/me".formatted(donoDaFazenda.fazendaId());
    }

    private String papelDe(Conta membro, Conta donoDaFazenda) {
        return jdbc.queryForObject("select papel from fazenda_membros where usuario_id = ? and fazenda_id = ?",
                String.class, membro.usuarioId(), donoDaFazenda.fazendaId());
    }

    private Object removidoEm(Conta membro, Conta donoDaFazenda) {
        return jdbc.queryForObject("select removed_at from fazenda_membros where usuario_id = ? and fazenda_id = ?",
                Object.class, membro.usuarioId(), donoDaFazenda.fazendaId());
    }
}
