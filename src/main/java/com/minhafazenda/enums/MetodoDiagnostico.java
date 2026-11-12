package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MetodoDiagnostico implements ValorNoBanco {
    TOQUE("toque"),
    ULTRASSOM("ultrassom"),
    OUTRO("outro");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<MetodoDiagnostico> {
        public Conversor() {
            super(MetodoDiagnostico.class);
        }
    }
}
