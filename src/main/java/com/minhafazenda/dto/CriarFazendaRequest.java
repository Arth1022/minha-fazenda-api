package com.minhafazenda.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarFazendaRequest(UUID id, @NotBlank @Size(max = 100) String nome) {
}
