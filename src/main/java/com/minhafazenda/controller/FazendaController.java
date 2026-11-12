package com.minhafazenda.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.minhafazenda.dto.CriarFazendaRequest;
import com.minhafazenda.dto.FazendaResponse;
import com.minhafazenda.dto.RenomearFazendaRequest;
import com.minhafazenda.security.UsuarioAutenticado;
import com.minhafazenda.service.FazendaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/fazendas")
@RequiredArgsConstructor
public class FazendaController {

    private final FazendaService fazendaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    FazendaResponse criar(UsuarioAutenticado usuario, @Valid @RequestBody CriarFazendaRequest request) {
        return FazendaResponse.de(fazendaService.criar(usuario.id(), request.id(), request.nome()));
    }

    @PatchMapping("/{id}")
    FazendaResponse renomear(UsuarioAutenticado usuario, @PathVariable UUID id,
            @Valid @RequestBody RenomearFazendaRequest request) {
        return FazendaResponse.de(fazendaService.renomear(usuario.id(), id, request.nome()));
    }
}
