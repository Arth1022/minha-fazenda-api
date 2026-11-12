package com.minhafazenda.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * @param privateKey           chave RSA privada em PEM PKCS#8
 * @param permitirChaveEfemera gera um par RSA na subida quando não há chave; só no perfil dev
 */
@Validated
@ConfigurationProperties("jwt")
public record JwtProperties(
        String privateKey,
        @NotBlank String kid,
        @Min(1) int accessMinutos,
        @Min(1) int refreshDias,
        @Min(1) int powersyncMinutos,
        @Min(1) int cacheSessaoSegundos,
        boolean permitirChaveEfemera) {

    public Duration validadeAccess() {
        return Duration.ofMinutes(accessMinutos);
    }

    public Duration validadeRefresh() {
        return Duration.ofDays(refreshDias);
    }

    public Duration validadePowerSync() {
        return Duration.ofMinutes(powersyncMinutos);
    }

    public Duration validadeCacheSessao() {
        return Duration.ofSeconds(cacheSessaoSegundos);
    }
}
