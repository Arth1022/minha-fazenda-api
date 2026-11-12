package com.minhafazenda.infra;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class FormatoImagemTest {

    @Test
    void reconheceCadaFormatoPelaAssinatura() {
        assertThat(FormatoImagem.detectar(new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1}))
                .contains(FormatoImagem.JPEG);
        assertThat(FormatoImagem.detectar(new byte[] {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0}))
                .contains(FormatoImagem.PNG);
        assertThat(FormatoImagem.detectar("RIFF\u0010\u0000\u0000\u0000WEBPVP8 ".getBytes(StandardCharsets.ISO_8859_1)))
                .contains(FormatoImagem.WEBP);
    }

    @Test
    void naoReconheceOutrosConteudos() {
        assertThat(FormatoImagem.detectar("MZ executavel".getBytes(StandardCharsets.ISO_8859_1))).isEmpty();
        assertThat(FormatoImagem.detectar("RIFF\u0010\u0000\u0000\u0000WAVEfmt ".getBytes(StandardCharsets.ISO_8859_1)))
                .isEmpty();
        assertThat(FormatoImagem.detectar("GIF89a".getBytes(StandardCharsets.ISO_8859_1))).isEmpty();
        assertThat(FormatoImagem.detectar(new byte[0])).isEmpty();
    }
}
