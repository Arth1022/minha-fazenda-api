package com.minhafazenda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("permissoes")
public record PermissoesProperties(boolean collaboratorPodeApagar) {
}
