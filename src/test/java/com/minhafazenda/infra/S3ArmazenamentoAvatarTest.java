package com.minhafazenda.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

import com.minhafazenda.config.StorageConfig;
import com.minhafazenda.config.StorageProperties;

/** Exercita o cliente S3 configurado pela aplicação contra um servidor compatível com S3. */
@Testcontainers
class S3ArmazenamentoAvatarTest {

    private static final int PORTA_HTTP = 9090;
    private static final String BUCKET = "avatars";
    private static final String URL_PUBLICA = "https://projeto.teste/storage/v1/object/public/avatars";
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 1, 2, 3};

    @Container
    private static final GenericContainer<?> SERVIDOR_S3 =
            new GenericContainer<>("adobe/s3mock:5.2.3").withExposedPorts(PORTA_HTTP);

    private static S3Client s3;
    private static S3ArmazenamentoAvatar armazenamento;

    @BeforeAll
    static void conectar() {
        String endpoint = "http://%s:%d".formatted(SERVIDOR_S3.getHost(), SERVIDOR_S3.getMappedPort(PORTA_HTTP));
        StorageProperties properties = new StorageProperties(endpoint, "us-east-1", "chave", "segredo", BUCKET,
                URL_PUBLICA);
        s3 = new StorageConfig().s3Client(properties);
        s3.createBucket(bucket -> bucket.bucket(BUCKET));
        armazenamento = new S3ArmazenamentoAvatar(s3, properties);
    }

    @Test
    void salvaOJpegNoCaminhoEDevolveAUrlPublica() {
        String url = armazenamento.salvar("usuario-1/foto.jpg", JPEG);

        assertThat(url).isEqualTo(URL_PUBLICA + "/usuario-1/foto.jpg");
        assertThat(s3.getObjectAsBytes(objeto -> objeto.bucket(BUCKET).key("usuario-1/foto.jpg")).asByteArray())
                .isEqualTo(JPEG);
        assertThat(s3.headObject(objeto -> objeto.bucket(BUCKET).key("usuario-1/foto.jpg")).contentType())
                .isEqualTo("image/jpeg");
    }

    @Test
    void apagaOArquivoApontadoPelaUrlPublica() {
        String url = armazenamento.salvar("usuario-2/foto.jpg", JPEG);

        armazenamento.apagar(url);

        assertThat(caminhosCom("usuario-2/")).isEmpty();
    }

    @Test
    void ignoraUrlQueNaoEDoBucket() {
        armazenamento.salvar("usuario-3/foto.jpg", JPEG);

        assertThatCode(() -> armazenamento.apagar("https://outro.site/usuario-3/foto.jpg"))
                .doesNotThrowAnyException();

        assertThat(caminhosCom("usuario-3/")).containsExactly("usuario-3/foto.jpg");
    }

    private static List<String> caminhosCom(String prefixo) {
        return s3.listObjectsV2(lista -> lista.bucket(BUCKET).prefix(prefixo)).contents().stream()
                .map(S3Object::key)
                .toList();
    }
}
