package com.minhafazenda.dto;

import jakarta.validation.constraints.NotBlank;

public record AceitarConviteRequest(@NotBlank String token) {
}
