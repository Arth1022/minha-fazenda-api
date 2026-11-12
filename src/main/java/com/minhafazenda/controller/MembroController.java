package com.minhafazenda.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.minhafazenda.dto.AlterarPapelRequest;
import com.minhafazenda.security.UsuarioAutenticado;
import com.minhafazenda.service.MembroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/fazendas/{fazendaId}/membros")
@ResponseStatus(HttpStatus.NO_CONTENT)
@RequiredArgsConstructor
public class MembroController {

    private final MembroService membroService;

    @PatchMapping("/{usuarioId}")
    void alterarPapel(UsuarioAutenticado usuario, @PathVariable UUID fazendaId, @PathVariable UUID usuarioId,
            @Valid @RequestBody AlterarPapelRequest request) {
        membroService.alterarPapel(usuario.id(), fazendaId, usuarioId, request.papel());
    }

    @DeleteMapping("/{usuarioId}")
    void remover(UsuarioAutenticado usuario, @PathVariable UUID fazendaId, @PathVariable UUID usuarioId) {
        membroService.remover(usuario.id(), fazendaId, usuarioId);
    }

    @DeleteMapping("/me")
    void sair(UsuarioAutenticado usuario, @PathVariable UUID fazendaId) {
        membroService.sair(usuario.id(), fazendaId);
    }
}
