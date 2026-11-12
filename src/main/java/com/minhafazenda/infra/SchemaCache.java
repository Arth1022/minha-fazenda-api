package com.minhafazenda.infra;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.minhafazenda.config.SyncProperties;

import lombok.RequiredArgsConstructor;

/**
 * Colunas das tabelas da whitelist, lidas do information_schema na subida (depois do Liquibase).
 * Uma coluna criada por changeset passa a ser aceita pelo sync sem mudança de código.
 */
@Component
@RequiredArgsConstructor
public class SchemaCache implements SmartInitializingSingleton {

    private static final Set<String> TABELAS_PROTEGIDAS = Set.of("usuarios", "sessoes", "fazendas",
            "fazenda_membros", "convites", "sync_rejeicoes", "auditoria_fazenda");

    private static final String SQL_COLUNAS = """
            SELECT column_name, udt_name
            FROM information_schema.columns
            WHERE table_schema = current_schema() AND table_name = ?
            """;

    private final JdbcTemplate jdbc;
    private final SyncProperties properties;

    private volatile Map<String, TabelaSchema> tabelas = Map.of();

    @Override
    public void afterSingletonsInstantiated() {
        recarregar();
    }

    public void recarregar() {
        Map<String, TabelaSchema> carregadas = new HashMap<>();
        properties.tabelas().forEach(nome -> carregadas.put(nome, carregar(nome)));
        tabelas = Map.copyOf(carregadas);
    }

    public Optional<TabelaSchema> buscar(String nome) {
        return Optional.ofNullable(tabelas.get(nome));
    }

    private TabelaSchema carregar(String nome) {
        if (TABELAS_PROTEGIDAS.contains(nome)) {
            throw new IllegalStateException("sync.tabelas não pode conter a tabela protegida " + nome);
        }
        Map<String, String> tipoPorColuna = new HashMap<>();
        jdbc.query(SQL_COLUNAS, linha -> {
            tipoPorColuna.put(linha.getString("column_name"), linha.getString("udt_name"));
        }, nome);
        if (!tipoPorColuna.containsKey(TabelaSchema.FAZENDA_ID)) {
            throw new IllegalStateException("sync.tabelas: a tabela %s não existe ou não tem fazenda_id".formatted(nome));
        }
        return new TabelaSchema(nome, Map.copyOf(tipoPorColuna));
    }
}
