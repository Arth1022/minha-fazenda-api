package com.minhafazenda.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;

class NomeUsuarioTest {

    @Test
    void removeEspacosDasPontas() {
        assertThat(NomeUsuario.normalizar("  Álvaro  ")).isEqualTo("Álvaro");
    }

    @Test
    void aceitaDeDoisASessentaCaracteres() {
        assertThat(NomeUsuario.normalizar("Jó")).isEqualTo("Jó");
        assertThat(NomeUsuario.normalizar("A".repeat(60))).hasSize(60);
    }

    @Test
    void recusaNomeCurtoLongoOuAusente() {
        for (String invalido : new String[] {"A", " A ", "A".repeat(61), "", null}) {
            assertThatThrownBy(() -> NomeUsuario.normalizar(invalido))
                    .isInstanceOfSatisfying(ApiException.class,
                            erro -> assertThat(erro.getCodigo()).isEqualTo(CodigoErro.VALIDACAO));
        }
    }
}
