package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Combustivel implements ValorNoBanco {
    DIESEL("diesel"),
    GASOLINA("gasolina"),
    ETANOL("etanol"),
    FLEX("flex"),
    ELETRICO("eletrico"),
    NENHUM("nenhum");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<Combustivel> {
        public Conversor() {
            super(Combustivel.class);
        }
    }
}
