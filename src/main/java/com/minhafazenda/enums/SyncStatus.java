package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SyncStatus implements ValorNoBanco {
    PENDENTE("pendente"),
    SINCRONIZADO("sincronizado");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<SyncStatus> {
        public Conversor() {
            super(SyncStatus.class);
        }
    }
}
