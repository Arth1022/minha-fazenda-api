package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoMaquina implements ValorNoBanco {
    TRATOR("trator"),
    COLHEITADEIRA("colheitadeira"),
    PULVERIZADOR("pulverizador"),
    PLANTADEIRA("plantadeira"),
    IMPLEMENTO("implemento"),
    CAMINHAO("caminhao"),
    UTILITARIO("utilitario"),
    ORDENHADEIRA("ordenhadeira"),
    OUTRO("outro");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<TipoMaquina> {
        public Conversor() {
            super(TipoMaquina.class);
        }
    }
}
