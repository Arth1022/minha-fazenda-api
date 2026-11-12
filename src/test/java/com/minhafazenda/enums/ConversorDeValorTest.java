package com.minhafazenda.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ConversorDeValorTest {

    private final Sexo.Conversor conversor = new Sexo.Conversor();

    @Test
    void gravaOValorDoAppENaoONomeDaConstante() {
        assertThat(conversor.convertToDatabaseColumn(Sexo.FEMEA)).isEqualTo("F");
        assertThat(conversor.convertToEntityAttribute("M")).isEqualTo(Sexo.MACHO);
    }

    @Test
    void nuloContinuaNulo() {
        assertThat(conversor.convertToDatabaseColumn(null)).isNull();
        assertThat(conversor.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void valorDesconhecidoFalhaComMensagemClara() {
        assertThatThrownBy(() -> conversor.convertToEntityAttribute("X"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sexo")
                .hasMessageContaining("X");
    }
}
