package com.minhafazenda.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.minhafazenda.config.PowerSyncProperties;
import com.minhafazenda.dto.LoginRequest;
import com.minhafazenda.dto.LoginResponse;
import com.minhafazenda.dto.LogoutRequest;
import com.minhafazenda.dto.PowerSyncTokenResponse;
import com.minhafazenda.dto.RefreshRequest;
import com.minhafazenda.dto.RegistroRequest;
import com.minhafazenda.dto.RegistroResponse;
import com.minhafazenda.dto.TokensResponse;
import com.minhafazenda.security.UsuarioAutenticado;
import com.minhafazenda.service.ContaService;
import com.minhafazenda.service.JwtService;
import com.minhafazenda.service.SessaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ContaService contaService;
    private final SessaoService sessaoService;
    private final JwtService jwtService;
    private final PowerSyncProperties powerSync;

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    RegistroResponse registrar(@Valid @RequestBody RegistroRequest request) {
        return contaService.registrar(request);
    }

    @PostMapping("/login")
    LoginResponse entrar(@Valid @RequestBody LoginRequest request) {
        return contaService.entrar(request);
    }

    @PostMapping("/refresh")
    TokensResponse renovar(@Valid @RequestBody RefreshRequest request) {
        return TokensResponse.de(sessaoService.renovar(request.refreshToken(), request.deviceId()));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void sair(UsuarioAutenticado usuario, @Valid @RequestBody LogoutRequest request) {
        sessaoService.encerrar(usuario.id(), request.refreshToken());
    }

    @GetMapping("/powersync-token")
    PowerSyncTokenResponse tokenPowerSync(UsuarioAutenticado usuario) {
        return PowerSyncTokenResponse.de(powerSync.url(), jwtService.emitirTokenPowerSync(usuario.id()));
    }
}
