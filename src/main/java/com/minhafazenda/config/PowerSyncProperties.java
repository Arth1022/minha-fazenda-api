package com.minhafazenda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

/** URL da instância PowerSync: é o endpoint devolvido ao app e o audience do token. */
// TODO(decisao): PowerSync Cloud ou self-host; a URL vem de variável de ambiente e serve aos dois.
@Validated
@ConfigurationProperties("powersync")
public record PowerSyncProperties(@NotBlank String url) {
}
