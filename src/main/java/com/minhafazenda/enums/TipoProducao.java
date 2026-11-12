package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoProducao implements ValorNoBanco {
    LEITE("leite"),
    CORTE("corte"),
    MISTO("misto");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<TipoProducao> {
        public Conversor() {
            super(TipoProducao.class);
        }
    }
}
