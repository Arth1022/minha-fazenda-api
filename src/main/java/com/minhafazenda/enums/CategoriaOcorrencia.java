package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoriaOcorrencia implements ValorNoBanco {
    MOTOR("motor"),
    HIDRAULICO("hidraulico"),
    PNEU("pneu"),
    ELETRICO("eletrico"),
    VAZAMENTO("vazamento"),
    OUTRO("outro");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<CategoriaOcorrencia> {
        public Conversor() {
            super(CategoriaOcorrencia.class);
        }
    }
}
