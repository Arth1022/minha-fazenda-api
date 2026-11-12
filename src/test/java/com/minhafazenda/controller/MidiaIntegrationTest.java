package com.minhafazenda.controller;

import static com.minhafazenda.apoio.Respostas.erro;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.minhafazenda.apoio.ArmazenamentoMidiaEmMemoria;
import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.JpegSegmentos;
import com.minhafazenda.midia.FormatoMidia;

class MidiaIntegrationTest extends IntegracaoTest {

    /** Cabeçalho de um M4A gravado pelo expo-audio (caixa ftyp com a marca "M4A "). */
    private static final byte[] M4A = concatenar(new byte[] {0, 0, 0, 0x20}, ascii("ftypM4A "), new byte[64]);

    @Autowired
    private ArmazenamentoMidiaEmMemoria armazenamento;

    @Test
    void fotoVaiParaAPastaDeImagensDaFazendaSemExif() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID id = UUID.randomUUID();

        var resposta = api.enviarMidia(conta, conta.fazendaId(), "imagem", id, recurso("imagens/foto-com-gps.jpg"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caminho").value(conta.fazendaId() + "/imagens/" + id + ".jpg"))
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.expiraEm").exists());

        String caminho = api.json(resposta).get("caminho").asString();
        var salvo = armazenamento.arquivo(caminho);
        assertThat(salvo.contentType()).isEqualTo("image/jpeg");
        assertThat(JpegSegmentos.de(salvo.conteudo()).metadados()).isEmpty();
    }

    @Test
    void audioVaiParaAPastaDeAudiosComOFormatoDetectado() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID id = UUID.randomUUID();

        api.enviarMidia(conta, conta.fazendaId(), "audio", id, M4A)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caminho").value(conta.fazendaId() + "/audios/" + id + ".m4a"));

        assertThat(armazenamento.arquivo(conta.fazendaId() + "/audios/" + id + ".m4a").contentType())
                .isEqualTo(FormatoMidia.M4A.contentType());
    }

    @Test
    void reenviarComOMesmoIdCaiNoMesmoCaminho() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID id = UUID.randomUUID();

        String primeiro = api.json(api.enviarMidia(conta, conta.fazendaId(), "audio", id, M4A)).get("caminho").asString();
        String segundo = api.json(api.enviarMidia(conta, conta.fazendaId(), "audio", id, M4A)).get("caminho").asString();

        assertThat(segundo).isEqualTo(primeiro);
    }

    @Test
    void fotoEnviadaComoAudioERecusada() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.enviarMidia(conta, conta.fazendaId(), "audio", null, recurso("imagens/foto-com-gps.jpg"))
                .andExpect(erro(415, "FORMATO_INVALIDO"));
    }

    @Test
    void tipoDesconhecidoERecusado() throws Exception {
        Conta conta = api.registrarComFazenda();

        api.enviarMidia(conta, conta.fazendaId(), "video", null, M4A).andExpect(erro(400, "VALIDACAO"));
    }

    @Test
    void quemNaoEMembroNaoEnviaNemLe() throws Exception {
        Conta dono = api.registrarComFazenda();
        Conta outro = api.registrarComFazenda();
        String caminho = api.json(api.enviarMidia(dono, dono.fazendaId(), "audio", null, M4A)).get("caminho").asString();

        api.enviarMidia(outro, dono.fazendaId(), "audio", null, M4A).andExpect(erro(403, "SEM_PERMISSAO"));
        api.get("/fazendas/" + dono.fazendaId() + "/midias/url?caminho=" + caminho, outro)
                .andExpect(erro(403, "SEM_PERMISSAO"));
    }

    @Test
    void caminhoDeOutraFazendaNaoELidoPelaRotaDaPropria() throws Exception {
        Conta dono = api.registrarComFazenda();
        Conta outro = api.registrarComFazenda();
        String caminho = api.json(api.enviarMidia(dono, dono.fazendaId(), "audio", null, M4A)).get("caminho").asString();

        api.get("/fazendas/" + outro.fazendaId() + "/midias/url?caminho=" + caminho, outro)
                .andExpect(erro(403, "SEM_PERMISSAO"));
    }

    @Test
    void colaboradorLeAMidiaDaFazendaPorUrlAssinada() throws Exception {
        Conta dono = api.registrarComFazenda();
        Conta colaborador = api.registrarCollaboratorNaFazendaDe(dono);
        String caminho = api.json(api.enviarMidia(dono, dono.fazendaId(), "audio", null, M4A)).get("caminho").asString();

        api.get("/fazendas/" + dono.fazendaId() + "/midias/url?caminho=" + caminho, colaborador)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caminho").value(caminho))
                .andExpect(jsonPath("$.url").value(org.hamcrest.Matchers.containsString("X-Amz-Expires=3600")));
    }

    @Test
    void urlDeArquivoQueNaoExisteE404EApagarRemove() throws Exception {
        Conta dono = api.registrarComFazenda();
        String caminho = api.json(api.enviarMidia(dono, dono.fazendaId(), "audio", null, M4A)).get("caminho").asString();

        api.delete("/fazendas/" + dono.fazendaId() + "/midias?caminho=" + caminho, dono)
                .andExpect(status().isNoContent());

        assertThat(armazenamento.existe(caminho)).isFalse();
        api.get("/fazendas/" + dono.fazendaId() + "/midias/url?caminho=" + caminho, dono)
                .andExpect(erro(404, "NAO_ENCONTRADO"));
    }

    @Test
    void caminhoForaDoFormatoERecusado() throws Exception {
        Conta dono = api.registrarComFazenda();

        api.get("/fazendas/" + dono.fazendaId() + "/midias/url?caminho=../../avatars/x.jpg", dono)
                .andExpect(erro(400, "VALIDACAO"));
    }

    @Test
    void audioAcimaDoLimiteERecusado() throws Exception {
        Conta conta = api.registrarComFazenda();
        byte[] grande = concatenar(M4A, new byte[20 * 1024 * 1024]);

        api.enviarMidia(conta, conta.fazendaId(), "audio", null, grande).andExpect(erro(413, "ARQUIVO_GRANDE"));
    }

    private static byte[] recurso(String caminho) throws IOException {
        try (InputStream entrada = MidiaIntegrationTest.class.getClassLoader().getResourceAsStream(caminho)) {
            return entrada.readAllBytes();
        }
    }

    private static byte[] ascii(String texto) {
        return texto.getBytes(StandardCharsets.US_ASCII);
    }

    private static byte[] concatenar(byte[]... partes) {
        int tamanho = 0;
        for (byte[] parte : partes) {
            tamanho += parte.length;
        }
        byte[] tudo = new byte[tamanho];
        int posicao = 0;
        for (byte[] parte : partes) {
            System.arraycopy(parte, 0, tudo, posicao, parte.length);
            posicao += parte.length;
        }
        return tudo;
    }
}
