package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MetodoPesagem implements ValorNoBanco {
    BALANCA("balanca"),
    FITA("fita");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<MetodoPesagem> {
        public Conversor() {
            super(MetodoPesagem.class);
        }
    }
}
