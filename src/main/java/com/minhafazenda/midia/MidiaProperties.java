package com.minhafazenda.midia;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Bucket privado das mídias das fazendas (fotos, áudios, comprovantes), no mesmo Supabase
 * Storage do avatar (mesmo endpoint e chaves S3 de {@code storage.*}).
 *
 * @param bucket             nome do bucket privado
 * @param validadeUrlMinutos por quanto tempo a URL assinada de leitura vale
 */
@Validated
@ConfigurationProperties("midia")
public record MidiaProperties(@NotBlank String bucket, @Min(1) int validadeUrlMinutos) {
}
