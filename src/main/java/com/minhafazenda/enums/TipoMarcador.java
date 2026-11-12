package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoMarcador implements ValorNoBanco {
    PRAGA("praga"),
    DANINHA("daninha"),
    EROSAO("erosao"),
    OUTRO("outro");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<TipoMarcador> {
        public Conversor() {
            super(TipoMarcador.class);
        }
    }
}
