package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoTalhao implements ValorNoBanco {
    PASTO("pasto"),
    LAVOURA("lavoura"),
    RESERVA("reserva"),
    OUTRO("outro");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<TipoTalhao> {
        public Conversor() {
            super(TipoTalhao.class);
        }
    }
}
