package com.minhafazenda.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.minhafazenda.dto.AceitarConviteRequest;
import com.minhafazenda.dto.AceitarConviteResponse;
import com.minhafazenda.dto.ConviteResponse;
import com.minhafazenda.security.UsuarioAutenticado;
import com.minhafazenda.service.ConviteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ConviteController {

    private final ConviteService conviteService;

    @PostMapping("/fazendas/{fazendaId}/convites")
    @ResponseStatus(HttpStatus.CREATED)
    ConviteResponse criar(UsuarioAutenticado usuario, @PathVariable UUID fazendaId) {
        return conviteService.criar(usuario.id(), fazendaId);
    }

    @DeleteMapping("/convites/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void revogar(UsuarioAutenticado usuario, @PathVariable UUID id) {
        conviteService.revogar(usuario.id(), id);
    }

    @PostMapping("/convites/aceitar")
    AceitarConviteResponse aceitar(UsuarioAutenticado usuario, @Valid @RequestBody AceitarConviteRequest request) {
        return new AceitarConviteResponse(conviteService.aceitar(usuario.id(), request.token()));
    }
}
