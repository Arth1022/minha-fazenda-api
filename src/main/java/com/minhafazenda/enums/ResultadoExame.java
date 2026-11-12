package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultadoExame implements ValorNoBanco {
    NEGATIVO("negativo"),
    POSITIVO("positivo"),
    INCONCLUSIVO("inconclusivo");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<ResultadoExame> {
        public Conversor() {
            super(ResultadoExame.class);
        }
    }
}
