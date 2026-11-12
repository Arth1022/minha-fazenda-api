package com.minhafazenda.controller;

import static com.minhafazenda.apoio.OpDeSync.put;
import static com.minhafazenda.apoio.OpDeSync.putAnimal;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.entity.Animal;
import com.minhafazenda.entity.Talhao;
import com.minhafazenda.enums.Sexo;
import com.minhafazenda.enums.StatusAnimal;
import com.minhafazenda.enums.TipoTalhao;

import jakarta.persistence.EntityManager;

/** Tabelas de domínio do schema v1 do app, pelo POST /sync/upload. */
class DominioSyncIntegrationTest extends IntegracaoTest {

    private static final String GEOMETRIA =
            "{\"type\":\"Polygon\",\"coordinates\":[[[-47.1,-21.1],[-47.2,-21.1],[-47.2,-21.2],[-47.1,-21.1]]]}";

    @Autowired
    private EntityManager entityManager;

    @Test
    void talhaoSobeComOsTiposDoContratoSemPerderCasasDecimais() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID id = UUID.randomUUID();

        api.sync(conta, List.of(put("talhoes", id, Map.ofEntries(
                entry("fazenda_id", conta.fazendaId().toString()),
                entry("nome", "Pasto do Rio"),
                entry("tipo", "pasto"),
                entry("cor", "#22aa55"),
                entry("area_hectares", 12.3456789),
                entry("geometria_json", GEOMETRIA),
                entry("centro_lat", -21.123456789012),
                entry("centro_lng", -47.987654321098),
                entry("min_lat", -21.2),
                entry("max_lat", -21.1),
                entry("min_lng", -47.2),
                entry("max_lng", -47.1),
                entry("data_plantio", "2026-11-11T12:34:56.789Z")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicadas").value(1));

        Map<String, Object> linha = jdbc.queryForMap(
                "select centro_lat, area_hectares, geometria_json, tipo from talhoes where id = ?", id);
        assertThat((BigDecimal) linha.get("centro_lat")).isEqualByComparingTo("-21.123456789012");
        assertThat((BigDecimal) linha.get("area_hectares")).isEqualByComparingTo("12.3456789");
        assertThat(linha).containsEntry("geometria_json", GEOMETRIA).containsEntry("tipo", "pasto");

        Talhao talhao = entityManager.find(Talhao.class, id);
        assertThat(talhao.getTipo()).isEqualTo(TipoTalhao.PASTO);
        assertThat(talhao.getDataPlantio())
                .isEqualTo(OffsetDateTime.of(2026, 11, 11, 12, 34, 56, 789_000_000, ZoneOffset.UTC));
    }

    @Test
    void enumDoAppVoltaComoConstanteJavaPelaEntity() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID id = UUID.randomUUID();

        api.sync(conta, List.of(putAnimal(id, Map.of(
                "fazenda_id", conta.fazendaId().toString(),
                "sexo", "M",
                "ja_pariu", true,
                "crias_anteriores", 3))))
                .andExpect(jsonPath("$.aplicadas").value(1));

        Animal animal = entityManager.find(Animal.class, id);
        assertThat(animal.getSexo()).isEqualTo(Sexo.MACHO);
        assertThat(animal.getStatus()).isEqualTo(StatusAnimal.ATIVO);
        assertThat(animal.getJaPariu()).isTrue();
        assertThat(animal.getCriasAnteriores()).isEqualTo(3);
    }

    @Test
    void putSemColunaObrigatoriaERejeitadoSemDerrubarOLote() throws Exception {
        Conta conta = api.registrarComFazenda();
        UUID animal = UUID.randomUUID();
        UUID pesagem = UUID.randomUUID();
        String fazendaId = conta.fazendaId().toString();

        api.sync(conta, List.of(
                putAnimal(animal, Map.of("fazenda_id", fazendaId)),
                put("pesagens", pesagem, Map.of(
                        "fazenda_id", fazendaId,
                        "animal_id", animal.toString(),
                        "data", "2026-11-11T12:00:00.000Z",
                        "metodo", "balanca"))))
                .andExpect(jsonPath("$.aplicadas").value(1))
                .andExpect(jsonPath("$.rejeitadas[0].table").value("pesagens"))
                .andExpect(jsonPath("$.rejeitadas[0].codigo").value("VALIDACAO"));

        assertThat(jdbc.queryForObject("select count(*) from pesagens where id = ?", Integer.class, pesagem)).isZero();
    }
}
