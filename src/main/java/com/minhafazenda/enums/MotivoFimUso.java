package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MotivoFimUso implements ValorNoBanco {
    MANUAL("manual"),
    PARADA("parada"),
    PAUSA("pausa"),
    VELOCIDADE("velocidade"),
    ASSUMIDA("assumida"),
    FORA("fora");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<MotivoFimUso> {
        public Conversor() {
            super(MotivoFimUso.class);
        }
    }
}
