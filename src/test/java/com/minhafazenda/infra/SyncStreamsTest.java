package com.minhafazenda.infra;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/** Confere o powersync/sync-streams.yaml contra as regras da arquitetura. */
class SyncStreamsTest {

    private static Map<String, Map<String, Object>> streams;

    @BeforeAll
    @SuppressWarnings("unchecked")
    static void lerStreams() throws IOException {
        Map<String, Object> arquivo = lerYaml(Path.of("powersync", "sync-streams.yaml"));
        streams = (Map<String, Map<String, Object>>) arquivo.get("streams");
    }

    @Test
    void todoStreamFiltraPorMembroAtivoDoUsuarioDoToken() {
        assertThat(streams).isNotEmpty();
        streams.forEach((nome, stream) -> assertThat(semQuebrasDeLinha(stream))
                .as("stream %s", nome)
                .contains("FROM fazenda_membros")
                .contains("usuario_id = auth.user_id() AND removed_at IS NULL"));
    }

    @Test
    void streamDeColegasEnviaSoIdNomeEAvatar() {
        assertThat(semQuebrasDeLinha(streams.get("colegas"))).startsWith("SELECT id, nome, avatar_url FROM usuarios");
    }

    @Test
    @SuppressWarnings("unchecked")
    void temStreamParaFazendasMembrosECadaTabelaSincronizada() throws IOException {
        Map<String, Object> configuracao = lerYaml(Path.of("src", "main", "resources", "application.yaml"));
        List<String> tabelas = (List<String>) ((Map<String, Object>) configuracao.get("sync")).get("tabelas");

        assertThat(streams).containsKeys("fazendas", "membros", "colegas");
        assertThat(streams.keySet()).containsAll(tabelas);
    }

    private static String semQuebrasDeLinha(Map<String, Object> stream) {
        return ((String) stream.get("query")).replaceAll("\\s+", " ").strip();
    }

    private static Map<String, Object> lerYaml(Path caminho) throws IOException {
        try (InputStream entrada = Files.newInputStream(caminho)) {
            return new Yaml().load(entrada);
        }
    }
}
