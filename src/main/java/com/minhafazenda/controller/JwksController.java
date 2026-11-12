package com.minhafazenda.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhafazenda.service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final JwtService jwtService;

    @GetMapping("/.well-known/jwks.json")
    Map<String, Object> jwks() {
        return jwtService.jwks();
    }
}
