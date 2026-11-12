package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MotivoParada implements ValorNoBanco {
    PAUSA("pausa"),
    CONTINUA("continua");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<MotivoParada> {
        public Conversor() {
            super(MotivoParada.class);
        }
    }
}
