package com.minhafazenda.dto;

import jakarta.validation.constraints.NotBlank;

public record AtualizarPerfilRequest(@NotBlank String nome) {
}
