package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusTarefa implements ValorNoBanco {
    ABERTA("aberta"),
    EM_ANDAMENTO("em_andamento"),
    CONCLUIDA("concluida"),
    CANCELADA("cancelada");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<StatusTarefa> {
        public Conversor() {
            super(StatusTarefa.class);
        }
    }
}
