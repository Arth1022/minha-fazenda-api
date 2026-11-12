package com.minhafazenda.dto;

import java.util.UUID;

import com.minhafazenda.entity.Usuario;

public record RegistroResponse(String accessToken, String refreshToken, long expiresIn, UsuarioResponse usuario,
        UUID fazendaId) {

    public static RegistroResponse de(SessaoAberta sessao, Usuario usuario, UUID fazendaId) {
        return new RegistroResponse(sessao.accessToken().valor(), sessao.refreshToken(),
                sessao.accessToken().expiraEmSegundos(), UsuarioResponse.de(usuario), fazendaId);
    }
}
