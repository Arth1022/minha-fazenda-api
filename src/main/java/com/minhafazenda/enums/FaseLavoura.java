package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FaseLavoura implements ValorNoBanco {
    PREPARO("preparo"),
    VEGETATIVO("vegetativo"),
    COLHEITA("colheita"),
    POUSIO("pousio");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<FaseLavoura> {
        public Conversor() {
            super(FaseLavoura.class);
        }
    }
}
