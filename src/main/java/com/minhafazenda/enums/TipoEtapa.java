package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoEtapa implements ValorNoBanco {
    PREPARO("preparo"),
    PLANTIO("plantio"),
    TRATO("trato"),
    COLHEITA("colheita");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<TipoEtapa> {
        public Conversor() {
            super(TipoEtapa.class);
        }
    }
}
