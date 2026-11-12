package com.minhafazenda.dto;

import com.minhafazenda.entity.Usuario;

public record LoginResponse(String accessToken, String refreshToken, long expiresIn, UsuarioResponse usuario) {

    public static LoginResponse de(SessaoAberta sessao, Usuario usuario) {
        return new LoginResponse(sessao.accessToken().valor(), sessao.refreshToken(),
                sessao.accessToken().expiraEmSegundos(), UsuarioResponse.de(usuario));
    }
}
