package com.minhafazenda.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;

import org.junit.jupiter.api.Test;

class TokenSeguroTest {

    @Test
    void geraTrintaEDoisBytesAleatoriosEmBase64Url() {
        String token = TokenSeguro.gerar();

        assertThat(Base64.getUrlDecoder().decode(token)).hasSize(32);
        assertThat(token).doesNotContain("+", "/", "=");
        assertThat(TokenSeguro.gerar()).isNotEqualTo(token);
    }

    @Test
    void hashESha256EmHexadecimal() {
        assertThat(TokenSeguro.hash("abc"))
                .isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
    }
}
