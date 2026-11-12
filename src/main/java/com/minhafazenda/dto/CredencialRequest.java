package com.minhafazenda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Cada tipo de credencial usa os campos de que precisa: "senha" usa e-mail e senha; "dev", só o e-mail. */
public record CredencialRequest(@NotBlank String tipo, @Email String email, String senha) {
}
