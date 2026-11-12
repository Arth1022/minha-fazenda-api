package com.minhafazenda.dto;

import java.time.Instant;
import java.util.UUID;

public record ConviteResponse(UUID id, String url, String token, Instant expiraEm) {
}
