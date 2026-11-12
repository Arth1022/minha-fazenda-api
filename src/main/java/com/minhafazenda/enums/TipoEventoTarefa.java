package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoEventoTarefa implements ValorNoBanco {
    CRIADA("criada"),
    ATRIBUIDA("atribuida"),
    ASSUMIDA("assumida"),
    LIBERADA("liberada"),
    INICIADA("iniciada"),
    CONCLUIDA("concluida"),
    CANCELADA("cancelada"),
    REABERTA("reaberta"),
    OBSERVACAO("observacao");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<TipoEventoTarefa> {
        public Conversor() {
            super(TipoEventoTarefa.class);
        }
    }
}
