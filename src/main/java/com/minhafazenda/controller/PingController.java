package com.minhafazenda.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Resposta mínima para checagem de saúde e para o ping que mantém a API acordada no Render. */
@RestController
public class PingController {

    @GetMapping("/ping")
    ResponseEntity<Void> ping() {
        return ResponseEntity.noContent().build();
    }
}
