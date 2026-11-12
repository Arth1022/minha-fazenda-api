package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusCiclo implements ValorNoBanco {
    ATIVO("ativo"),
    ENCERRADO("encerrado"),
    CANCELADO("cancelado");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<StatusCiclo> {
        public Conversor() {
            super(StatusCiclo.class);
        }
    }
}
