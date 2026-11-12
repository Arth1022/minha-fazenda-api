package com.minhafazenda.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

/**
 * @param minSchemaVersion menor schemaVersion do app que a API ainda aceita
 * @param tabelas          whitelist das tabelas de domínio que o app pode gravar
 */
@Validated
@ConfigurationProperties("sync")
public record SyncProperties(int minSchemaVersion, @NotNull List<String> tabelas) {
}
