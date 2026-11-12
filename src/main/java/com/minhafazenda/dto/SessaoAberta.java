package com.minhafazenda.dto;

/** Par de tokens entregue ao app; o refresh em texto só existe aqui. */
public record SessaoAberta(AccessToken accessToken, String refreshToken) {
}
