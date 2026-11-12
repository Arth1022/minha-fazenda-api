package com.minhafazenda.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties("convite")
public record ConviteProperties(@NotBlank String linkBase, @Min(1) int validadeHoras, @Min(1) int usosMax) {

    public Duration validade() {
        return Duration.ofHours(validadeHoras);
    }

    public String urlPara(String token) {
        return linkBase + "/convite/" + token;
    }
}
