package com.minhafazenda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenomearFazendaRequest(@NotBlank @Size(max = 100) String nome) {
}
