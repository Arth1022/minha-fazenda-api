package com.minhafazenda.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.config.SyncProperties;

import tools.jackson.databind.json.JsonMapper;

class ContratoDeSchemaIntegrationTest extends IntegracaoTest {

    private static final Path CONTRATO_DE_COLUNAS = Path.of("contracts", "schema-colunas.json");

    @Autowired
    private SyncProperties syncProperties;

    @Autowired
    private JsonMapper json;

    @Test
    void todaTabelaDeDominioTemFazendaIdObrigatorioComFkEIndice() {
        assertThat(syncProperties.tabelas()).isNotEmpty();
        for (String tabela : syncProperties.tabelas()) {
            assertThat(jdbc.queryForObject("""
                    select is_nullable from information_schema.columns
                    where table_schema = 'public' and table_name = ? and column_name = 'fazenda_id'
                    """, String.class, tabela)).as("fazenda_id NOT NULL em %s", tabela).isEqualTo("NO");
            assertThat(jdbc.queryForObject("""
                    select count(*) from information_schema.table_constraints tc
                    join information_schema.key_column_usage kcu
                      on kcu.constraint_name = tc.constraint_name and kcu.table_schema = tc.table_schema
                    join information_schema.constraint_column_usage ccu
                      on ccu.constraint_name = tc.constraint_name and ccu.table_schema = tc.table_schema
                    where tc.constraint_type = 'FOREIGN KEY' and tc.table_name = ?
                      and kcu.column_name = 'fazenda_id' and ccu.table_name = 'fazendas'
                    """, Integer.class, tabela)).as("FK de fazenda_id em %s", tabela).isEqualTo(1);
            assertThat(jdbc.queryForObject("""
                    select count(*) from pg_indexes
                    where schemaname = 'public' and tablename = ? and indexdef like '%(fazenda_id)%'
                    """, Integer.class, tabela)).as("índice em fazenda_id de %s", tabela).isPositive();
        }
    }

    @Test
    void whitelistDoSyncNaoAceitaTabelaProtegida() {
        SchemaCache cache = new SchemaCache(jdbc, new SyncProperties(1, List.of("animais", "fazenda_membros")));

        assertThatIllegalStateException().isThrownBy(cache::recarregar).withMessageContaining("fazenda_membros");
    }

    @Test
    void publicationDoPowerSyncCobreTodasAsTabelas() {
        assertThat(jdbc.queryForObject("select puballtables from pg_publication where pubname = 'powersync'",
                Boolean.class)).isTrue();
    }

    @Test
    void todasAsTabelasTemRowLevelSecurityLigado() {
        assertThat(jdbc.queryForList("""
                select c.relname from pg_class c join pg_namespace n on n.oid = c.relnamespace
                where n.nspname = 'public' and c.relkind = 'r' and not c.relrowsecurity
                """, String.class)).isEmpty();
    }

    /** Gera o arquivo que o front compara com o schema do Drizzle. */
    @Test
    void exportaAsColunasDoBancoParaOContratoDoFront() throws Exception {
        List<Map<String, Object>> colunas = jdbc.queryForList("""
                SELECT table_name, column_name, data_type FROM information_schema.columns
                WHERE table_schema = 'public'
                ORDER BY table_name, ordinal_position
                """);

        Files.createDirectories(CONTRATO_DE_COLUNAS.getParent());
        json.writerWithDefaultPrettyPrinter().writeValue(CONTRATO_DE_COLUNAS.toFile(), colunas);

        assertThat(colunas).contains(Map.of("table_name", "animais", "column_name", "fazenda_id", "data_type", "uuid"));
    }
}
