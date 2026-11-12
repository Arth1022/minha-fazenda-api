package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AtividadeUso implements ValorNoBanco {
    PLANTIO("plantio"),
    PULVERIZACAO("pulverizacao"),
    GRADAGEM("gradagem"),
    TRANSPORTE("transporte"),
    TRATO("trato"),
    PARADO("parado"),
    OUTRO("outro");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<AtividadeUso> {
        public Conversor() {
            super(AtividadeUso.class);
        }
    }
}
