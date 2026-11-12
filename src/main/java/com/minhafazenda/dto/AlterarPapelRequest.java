package com.minhafazenda.dto;

import com.minhafazenda.enums.Papel;

import jakarta.validation.constraints.NotNull;

public record AlterarPapelRequest(@NotNull Papel papel) {
}
