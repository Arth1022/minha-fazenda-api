package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sexo implements ValorNoBanco {
    FEMEA("F"),
    MACHO("M");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<Sexo> {
        public Conversor() {
            super(Sexo.class);
        }
    }
}
