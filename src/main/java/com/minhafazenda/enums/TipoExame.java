package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoExame implements ValorNoBanco {
    BRUCELOSE("brucelose"),
    TUBERCULOSE("tuberculose"),
    OUTRO("outro");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<TipoExame> {
        public Conversor() {
            super(TipoExame.class);
        }
    }
}
