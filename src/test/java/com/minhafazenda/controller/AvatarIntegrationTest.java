package com.minhafazenda.controller;

import static com.minhafazenda.apoio.Respostas.erro;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.minhafazenda.apoio.ArmazenamentoEmMemoria;
import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.JpegSegmentos;
import com.minhafazenda.apoio.Respostas;
import com.minhafazenda.infra.FormatoImagem;

class AvatarIntegrationTest extends IntegracaoTest {

    private static final String WEBP_1X1 = "UklGRiIAAABXRUJQVlA4IBYAAAAwAQCdASoBAAEADsD+JaQAA3AAAAAA";

    @Autowired
    private ArmazenamentoEmMemoria armazenamento;

    @Test
    void fotoComGpsNoExifSaiSemNenhumMetadado() throws Exception {
        byte[] original = recurso("imagens/foto-com-gps.jpg");
        assertThat(JpegSegmentos.de(original).temExif()).as("o arquivo de exemplo precisa ter EXIF").isTrue();
        Conta conta = api.registrarComFazenda();

        String url = enviarComSucesso(conta, "foto.jpg", "image/jpeg", original);

        byte[] salvo = armazenamento.conteudo(url);
        assertThat(JpegSegmentos.de(salvo).metadados()).isEmpty();
        assertThat(new String(salvo, StandardCharsets.ISO_8859_1)).doesNotContain("Exif");
    }

    @Test
    void regravaAFotoEmJpegQuadradoDe512Pixels() throws Exception {
        Conta conta = api.registrarComFazenda();

        String url = enviarComSucesso(conta, "foto.jpg", "image/jpeg", recurso("imagens/foto-com-gps.jpg"));

        byte[] salvo = armazenamento.conteudo(url);
        BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(salvo));
        assertThat(FormatoImagem.detectar(salvo)).contains(FormatoImagem.JPEG);
        assertThat(imagem.getWidth()).isEqualTo(512);
        assertThat(imagem.getHeight()).isEqualTo(512);
        assertThat(url).matches(".*/" + conta.usuarioId() + "/[0-9a-f-]{36}\\.jpg");
        api.get("/usuarios/me", conta).andExpect(jsonPath("$.avatarUrl").value(url));
    }

    @Test
    void aceitaPngEWebpPeloConteudoMesmoComContentTypeErrado() throws Exception {
        Conta conta = api.registrarComFazenda();

        String urlDoPng = enviarComSucesso(conta, "foto.png", "application/octet-stream", pngComTransparencia());
        String urlDoWebp = enviarComSucesso(conta, "foto.webp", "text/plain", Base64.getDecoder().decode(WEBP_1X1));

        assertThat(FormatoImagem.detectar(armazenamento.conteudo(urlDoWebp))).contains(FormatoImagem.JPEG);
        assertThat(urlDoPng).isNotEqualTo(urlDoWebp);
    }

    @Test
    void executavelRenomeadoParaJpgERecusado() throws Exception {
        Conta conta = api.registrarComFazenda();
        byte[] executavel = "MZ\u0090\u0000\u0003\u0000\u0000\u0000 This program cannot be run in DOS mode."
                .getBytes(StandardCharsets.ISO_8859_1);

        api.enviarAvatar(conta, "foto.jpg", "image/jpeg", executavel).andExpect(erro(415, "FORMATO_INVALIDO"));

        assertThat(armazenamento.caminhosDe(conta.usuarioId())).isEmpty();
    }

    @Test
    void jpegCorrompidoERecusado() throws Exception {
        Conta conta = api.registrarComFazenda();
        byte[] corrompido = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 1, 2, 3, 4, 5, 6};

        api.enviarAvatar(conta, "foto.jpg", "image/jpeg", corrompido).andExpect(erro(415, "FORMATO_INVALIDO"));
    }

    @Test
    void arquivoAcimaDeCincoMegabytesERecusado() throws Exception {
        Conta conta = api.registrarComFazenda();
        byte[] grande = new byte[5 * 1024 * 1024 + 1];

        api.enviarAvatar(conta, "foto.jpg", "image/jpeg", grande).andExpect(erro(413, "ARQUIVO_GRANDE"));
    }

    @Test
    void trocarOAvatarDuasVezesDeixaSoUmArquivoNoBucket() throws Exception {
        Conta conta = api.registrarComFazenda();
        byte[] foto = recurso("imagens/foto-com-gps.jpg");

        String primeira = enviarComSucesso(conta, "a.jpg", "image/jpeg", foto);
        String segunda = enviarComSucesso(conta, "b.jpg", "image/jpeg", foto);

        assertThat(segunda).isNotEqualTo(primeira);
        assertThat(armazenamento.caminhosDe(conta.usuarioId())).hasSize(1);
        assertThat(armazenamento.conteudo(segunda)).isNotNull();
    }

    @Test
    void removerOAvatarApagaOArquivoEZeraAUrl() throws Exception {
        Conta conta = api.registrarComFazenda();
        enviarComSucesso(conta, "a.jpg", "image/jpeg", recurso("imagens/foto-com-gps.jpg"));

        api.delete("/usuarios/me/avatar", conta).andExpect(status().isNoContent());

        assertThat(armazenamento.caminhosDe(conta.usuarioId())).isEmpty();
        api.get("/usuarios/me", conta).andExpect(jsonPath("$.avatarUrl").value((Object) null));
    }

    private String enviarComSucesso(Conta conta, String nome, String contentType, byte[] conteudo) throws Exception {
        return api.json(api.enviarAvatar(conta, nome, contentType, conteudo).andExpect(status().isOk()))
                .get("avatarUrl").asString();
    }

    private static byte[] pngComTransparencia() throws IOException {
        BufferedImage imagem = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        ImageIO.write(imagem, "png", saida);
        return saida.toByteArray();
    }

    private static byte[] recurso(String caminho) throws IOException {
        try (InputStream entrada = AvatarIntegrationTest.class.getClassLoader().getResourceAsStream(caminho)) {
            return entrada.readAllBytes();
        }
    }
}
