package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoriaMaquina implements ValorNoBanco {
    MOTORIZADA("motorizada"),
    IMPLEMENTO("implemento");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<CategoriaMaquina> {
        public Conversor() {
            super(CategoriaMaquina.class);
        }
    }
}
