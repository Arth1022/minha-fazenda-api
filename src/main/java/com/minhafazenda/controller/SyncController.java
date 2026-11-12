package com.minhafazenda.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhafazenda.dto.SyncUploadRequest;
import com.minhafazenda.dto.SyncUploadResponse;
import com.minhafazenda.security.UsuarioAutenticado;
import com.minhafazenda.service.SyncService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @PostMapping("/upload")
    SyncUploadResponse upload(UsuarioAutenticado usuario, @Valid @RequestBody SyncUploadRequest request) {
        return syncService.aplicar(usuario.id(), request);
    }
}
