package com.minhafazenda.infra;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.minhafazenda.config.StorageProperties;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3ArmazenamentoAvatar implements ArmazenamentoAvatar {

    private final S3Client s3;
    private final StorageProperties properties;

    @Override
    public String salvar(String caminho, byte[] jpeg) {
        PutObjectRequest requisicao = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(caminho)
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .build();
        s3.putObject(requisicao, RequestBody.fromBytes(jpeg));
        return prefixoPublico() + caminho;
    }

    @Override
    public void apagar(String url) {
        if (!url.startsWith(prefixoPublico())) {
            return;
        }
        String caminho = url.substring(prefixoPublico().length());
        s3.deleteObject(DeleteObjectRequest.builder().bucket(properties.bucket()).key(caminho).build());
    }

    private String prefixoPublico() {
        return properties.publicUrl() + "/";
    }
}
