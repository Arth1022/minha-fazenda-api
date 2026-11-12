package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PeriodoOrdenha implements ValorNoBanco {
    MANHA("manha"),
    TARDE("tarde"),
    NOITE("noite");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<PeriodoOrdenha> {
        public Conversor() {
            super(PeriodoOrdenha.class);
        }
    }
}
