package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrigemAbastecimento implements ValorNoBanco {
    FAZENDA("fazenda"),
    POSTO("posto");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<OrigemAbastecimento> {
        public Conversor() {
            super(OrigemAbastecimento.class);
        }
    }
}
