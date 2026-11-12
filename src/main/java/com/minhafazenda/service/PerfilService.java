package com.minhafazenda.service;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhafazenda.entity.Usuario;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final UsuarioRepository usuarios;
    private final ApplicationEventPublisher eventos;

    @Transactional(readOnly = true)
    public Usuario buscar(UUID usuarioId) {
        return usuarios.findById(usuarioId).orElseThrow(() -> new ApiException(CodigoErro.NAO_ENCONTRADO));
    }

    @Transactional
    public Usuario renomear(UUID usuarioId, String novoNome) {
        Usuario usuario = buscar(usuarioId);
        usuario.renomear(novoNome);
        return usuario;
    }

    @Transactional
    public void definirAvatar(UUID usuarioId, String url) {
        Usuario usuario = buscar(usuarioId);
        descartar(usuario.getAvatarUrl());
        usuario.definirAvatar(url);
    }

    @Transactional
    public void removerAvatar(UUID usuarioId) {
        Usuario usuario = buscar(usuarioId);
        descartar(usuario.getAvatarUrl());
        usuario.removerAvatar();
    }

    private void descartar(String urlAnterior) {
        if (urlAnterior != null) {
            eventos.publishEvent(new AvatarDescartado(urlAnterior));
        }
    }
}
