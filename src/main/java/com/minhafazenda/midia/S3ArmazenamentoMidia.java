package com.minhafazenda.midia;

import java.time.Duration;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@RequiredArgsConstructor
public class S3ArmazenamentoMidia implements ArmazenamentoMidia {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final MidiaProperties properties;

    @Override
    public void salvar(String caminho, byte[] conteudo, String contentType) {
        PutObjectRequest requisicao = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(caminho)
                .contentType(contentType)
                // O arquivo de um caminho nunca muda (o id é do registro): o celular pode guardar
                .cacheControl("private, max-age=31536000, immutable")
                .build();
        s3.putObject(requisicao, RequestBody.fromBytes(conteudo));
    }

    @Override
    public String urlAssinada(String caminho, Duration validade) {
        GetObjectPresignRequest requisicao = GetObjectPresignRequest.builder()
                .signatureDuration(validade)
                .getObjectRequest(GetObjectRequest.builder().bucket(properties.bucket()).key(caminho).build())
                .build();
        return presigner.presignGetObject(requisicao).url().toString();
    }

    @Override
    public boolean existe(String caminho) {
        try {
            s3.headObject(HeadObjectRequest.builder().bucket(properties.bucket()).key(caminho).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public void apagar(String caminho) {
        s3.deleteObject(DeleteObjectRequest.builder().bucket(properties.bucket()).key(caminho).build());
    }
}
