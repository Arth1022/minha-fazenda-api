package com.minhafazenda.dto;

import java.util.UUID;

import com.minhafazenda.entity.Usuario;

public record UsuarioResponse(UUID id, String nome, String avatarUrl) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getAvatarUrl());
    }
}
