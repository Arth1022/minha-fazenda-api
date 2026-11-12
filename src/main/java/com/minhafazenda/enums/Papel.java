package com.minhafazenda.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Papel {

    OWNER("owner"),
    COLLABORATOR("collaborator");

    @JsonValue
    private final String valor;

    @JsonCreator
    public static Papel de(String valor) {
        return Arrays.stream(values())
                .filter(papel -> papel.valor.equals(valor))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Papel desconhecido: " + valor));
    }
}
