package com.minhafazenda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

/**
 * Supabase Storage pela API compatível com S3.
 *
 * @param publicUrl base pública do bucket, sem barra no fim
 */
@Validated
@ConfigurationProperties("storage")
public record StorageProperties(
        @NotBlank String endpoint,
        @NotBlank String region,
        @NotBlank String key,
        @NotBlank String secret,
        @NotBlank String bucket,
        @NotBlank String publicUrl) {
}
