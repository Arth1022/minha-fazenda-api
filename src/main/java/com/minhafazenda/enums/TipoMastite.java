package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoMastite implements ValorNoBanco {
    CLINICA("clinica"),
    SUBCLINICA("subclinica");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<TipoMastite> {
        public Conversor() {
            super(TipoMastite.class);
        }
    }
}
