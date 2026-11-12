package com.minhafazenda.apoio;

import java.util.UUID;

/** Usuário registrado num teste, com os tokens e a fazenda devolvidos pelo registro. */
public record Conta(UUID usuarioId, String email, String deviceId, String accessToken, String refreshToken,
        UUID fazendaId) {

    public String bearer() {
        return "Bearer " + accessToken;
    }
}
