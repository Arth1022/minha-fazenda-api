package com.minhafazenda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Exatamente um entre nomeFazenda e conviteToken deve vir preenchido. */
public record RegistroRequest(
        @NotBlank String nome,
        @NotNull @Valid CredencialRequest credencial,
        @NotBlank String deviceId,
        @Size(max = 100) String nomeFazenda,
        String conviteToken) {
}
