package com.minhafazenda.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.minhafazenda.dto.AtualizarPerfilRequest;
import com.minhafazenda.dto.AvatarResponse;
import com.minhafazenda.dto.UsuarioResponse;
import com.minhafazenda.security.UsuarioAutenticado;
import com.minhafazenda.service.AvatarService;
import com.minhafazenda.service.PerfilService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios/me")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;
    private final AvatarService avatarService;

    @GetMapping
    UsuarioResponse buscar(UsuarioAutenticado usuario) {
        return UsuarioResponse.de(perfilService.buscar(usuario.id()));
    }

    @PatchMapping
    UsuarioResponse atualizar(UsuarioAutenticado usuario, @Valid @RequestBody AtualizarPerfilRequest request) {
        return UsuarioResponse.de(perfilService.renomear(usuario.id(), request.nome()));
    }

    @PutMapping(path = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    AvatarResponse trocarAvatar(UsuarioAutenticado usuario, @RequestPart("arquivo") MultipartFile arquivo) {
        return new AvatarResponse(avatarService.trocar(usuario.id(), arquivo));
    }

    @DeleteMapping("/avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removerAvatar(UsuarioAutenticado usuario) {
        avatarService.remover(usuario.id());
    }
}
