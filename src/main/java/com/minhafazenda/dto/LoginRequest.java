package com.minhafazenda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(@NotNull @Valid CredencialRequest credencial, @NotBlank String deviceId) {
}
