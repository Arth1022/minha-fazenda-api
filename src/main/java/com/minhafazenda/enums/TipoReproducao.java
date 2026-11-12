package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoReproducao implements ValorNoBanco {
    COBERTURA("cobertura"),
    INSEMINACAO("inseminacao");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<TipoReproducao> {
        public Conversor() {
            super(TipoReproducao.class);
        }
    }
}
