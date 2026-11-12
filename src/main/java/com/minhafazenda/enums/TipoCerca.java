package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoCerca implements ValorNoBanco {
    ARAME4("arame4"),
    ARAME5("arame5"),
    ELETRICA1("eletrica1"),
    ELETRICA2("eletrica2");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<TipoCerca> {
        public Conversor() {
            super(TipoCerca.class);
        }
    }
}
