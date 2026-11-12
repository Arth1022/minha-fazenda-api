package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusAnimal implements ValorNoBanco {
    ATIVO("ativo"),
    VENDIDO("vendido"),
    MORTO("morto"),
    DESCARTE("descarte");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<StatusAnimal> {
        public Conversor() {
            super(StatusAnimal.class);
        }
    }
}
