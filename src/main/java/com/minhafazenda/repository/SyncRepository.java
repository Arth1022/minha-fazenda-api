package com.minhafazenda.repository;

import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.minhafazenda.infra.TabelaSchema;
import com.minhafazenda.infra.ValorColuna;

import lombok.RequiredArgsConstructor;

/**
 * SQL do sync. Nomes de tabela e coluna vêm sempre do {@link TabelaSchema}; valores do app
 * entram só como parâmetros, com cast para o tipo real da coluna.
 */
@Repository
@RequiredArgsConstructor
public class SyncRepository {

    private final JdbcTemplate jdbc;

    public Optional<UUID> buscarFazendaDoRegistro(TabelaSchema tabela, UUID id) {
        String sql = "SELECT fazenda_id FROM %s WHERE id = ?".formatted(entreAspas(tabela.nome()));
        return jdbc.query(sql, (linha, numero) -> linha.getObject(1, UUID.class), id).stream().findFirst();
    }

    /**
     * Upsert do PUT. created_by só entra no insert, e a cláusula WHERE do conflito impede que
     * um registro mude de fazenda mesmo numa corrida entre dois lotes.
     */
    public int inserirOuAtualizar(TabelaSchema tabela, UUID id, UUID fazendaId, UUID criadoPor, List<ValorColuna> valores) {
        String nomeTabela = entreAspas(tabela.nome());
        String colunas = Stream.concat(
                Stream.of("id", "fazenda_id", "created_by", "server_updated_at"),
                valores.stream().map(valor -> entreAspas(valor.coluna())))
                .collect(Collectors.joining(", "));
        String marcadores = Stream.concat(
                Stream.of("?", "?", "?", "now()"),
                valores.stream().map(SyncRepository::marcador))
                .collect(Collectors.joining(", "));
        String atualizacoes = Stream.concat(
                valores.stream().map(valor -> "%1$s = EXCLUDED.%1$s".formatted(entreAspas(valor.coluna()))),
                Stream.of("server_updated_at = now()"))
                .collect(Collectors.joining(", "));
        String sql = """
                INSERT INTO %1$s (%2$s) VALUES (%3$s)
                ON CONFLICT (id) DO UPDATE SET %4$s
                WHERE %1$s.fazenda_id = EXCLUDED.fazenda_id
                """.formatted(nomeTabela, colunas, marcadores, atualizacoes);

        List<Object> parametros = new ArrayList<>(List.of(id, fazendaId, criadoPor));
        valores.forEach(valor -> parametros.add(valor.valor()));
        return executar(sql, parametros);
    }

    public int atualizar(TabelaSchema tabela, UUID id, List<ValorColuna> valores) {
        String atualizacoes = Stream.concat(
                valores.stream().map(valor -> "%s = %s".formatted(entreAspas(valor.coluna()), marcador(valor))),
                Stream.of("server_updated_at = now()"))
                .collect(Collectors.joining(", "));
        String sql = "UPDATE %s SET %s WHERE id = ?".formatted(entreAspas(tabela.nome()), atualizacoes);

        List<Object> parametros = new ArrayList<>();
        valores.forEach(valor -> parametros.add(valor.valor()));
        parametros.add(id);
        return executar(sql, parametros);
    }

    /** Soft delete idempotente: repetir a operação mantém o deleted_at original. */
    public int apagar(TabelaSchema tabela, UUID id) {
        String sql = """
                UPDATE %s SET deleted_at = COALESCE(deleted_at, now()), server_updated_at = now()
                WHERE id = ?
                """.formatted(entreAspas(tabela.nome()));
        return executar(sql, List.of(id));
    }

    /**
     * Cada operação roda num savepoint: um valor que o Postgres recusa desfaz só ela,
     * e a transação do lote continua utilizável.
     */
    private int executar(String sql, List<Object> parametros) {
        return jdbc.execute((ConnectionCallback<Integer>) conexao -> {
            Savepoint ponto = conexao.setSavepoint();
            try {
                int linhas = jdbc.update(sql, parametros.toArray());
                conexao.releaseSavepoint(ponto);
                return linhas;
            } catch (DataIntegrityViolationException e) {
                conexao.rollback(ponto);
                throw e;
            }
        });
    }

    private static String marcador(ValorColuna valor) {
        return "?::" + entreAspas(valor.tipo());
    }

    private static String entreAspas(String identificador) {
        return '"' + identificador.replace("\"", "\"\"") + '"';
    }
}
