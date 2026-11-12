package com.minhafazenda.enums;

import java.util.Arrays;

import jakarta.persistence.AttributeConverter;

/** Converte um enum {@link ValorNoBanco} para o texto da coluna e de volta. */
public abstract class ConversorDeValor<E extends Enum<E> & ValorNoBanco> implements AttributeConverter<E, String> {

    private final Class<E> tipo;

    protected ConversorDeValor(Class<E> tipo) {
        this.tipo = tipo;
    }

    @Override
    public String convertToDatabaseColumn(E valor) {
        return valor == null ? null : valor.getValor();
    }

    @Override
    public E convertToEntityAttribute(String coluna) {
        if (coluna == null) {
            return null;
        }
        return Arrays.stream(tipo.getEnumConstants())
                .filter(constante -> constante.getValor().equals(coluna))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Valor desconhecido para %s: %s".formatted(tipo.getSimpleName(), coluna)));
    }
}
