package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrigemLeitura implements ValorNoBanco {
    ABASTECIMENTO("abastecimento"),
    AVULSA("avulsa");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<OrigemLeitura> {
        public Conversor() {
            super(OrigemLeitura.class);
        }
    }
}
