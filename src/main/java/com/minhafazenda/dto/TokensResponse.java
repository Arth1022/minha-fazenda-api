package com.minhafazenda.dto;

public record TokensResponse(String accessToken, String refreshToken, long expiresIn) {

    public static TokensResponse de(SessaoAberta sessao) {
        return new TokensResponse(sessao.accessToken().valor(), sessao.refreshToken(),
                sessao.accessToken().expiraEmSegundos());
    }
}
