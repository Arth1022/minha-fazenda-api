package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusMaquina implements ValorNoBanco {
    ATIVA("ativa"),
    MANUTENCAO("manutencao"),
    INATIVA("inativa");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<StatusMaquina> {
        public Conversor() {
            super(StatusMaquina.class);
        }
    }
}
