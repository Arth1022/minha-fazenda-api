package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrigemUso implements ValorNoBanco {
    APP("app"),
    MANUAL("manual");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<OrigemUso> {
        public Conversor() {
            super(OrigemUso.class);
        }
    }
}
