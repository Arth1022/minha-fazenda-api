package com.minhafazenda.dto;

import java.time.Instant;

public record PowerSyncTokenResponse(String endpoint, String token, Instant expiresAt) {

    public static PowerSyncTokenResponse de(String endpoint, TokenPowerSync token) {
        return new PowerSyncTokenResponse(endpoint, token.valor(), token.expiraEm());
    }
}
