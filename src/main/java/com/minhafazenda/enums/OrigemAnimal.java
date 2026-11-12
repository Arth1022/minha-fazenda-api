package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrigemAnimal implements ValorNoBanco {
    NASCIDO("nascido"),
    COMPRADO("comprado");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<OrigemAnimal> {
        public Conversor() {
            super(OrigemAnimal.class);
        }
    }
}
