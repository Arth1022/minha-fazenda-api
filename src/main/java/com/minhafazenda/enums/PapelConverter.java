package com.minhafazenda.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PapelConverter implements AttributeConverter<Papel, String> {

    @Override
    public String convertToDatabaseColumn(Papel papel) {
        return papel == null ? null : papel.getValor();
    }

    @Override
    public Papel convertToEntityAttribute(String valor) {
        return valor == null ? null : Papel.de(valor);
    }
}
