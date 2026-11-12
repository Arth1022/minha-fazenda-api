package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoriaTarefa implements ValorNoBanco {
    CERCA("cerca"),
    ANIMAL("animal"),
    MAQUINA("maquina"),
    AGUA("agua"),
    PASTO("pasto"),
    OUTRO("outro");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<CategoriaTarefa> {
        public Conversor() {
            super(CategoriaTarefa.class);
        }
    }
}
