package com.minhafazenda.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ConversorDeValorTest {

    private final Sexo.Conversor conversor = new Sexo.Conversor();

    @Test
    void gravaOValorDoAppENaoONomeDaConstante() {
        assertThat(conversor.convertToDatabaseColumn(Sexo.FEMEA)).isEqualTo("F");
        assertThat(conversor.convertToEntityAttribute("M")).isEqualTo(Sexo.MACHO);
    }

    @Test
    void nuloContinuaNulo() {
        assertThat(conversor.convertToDatabaseColumn(null)).isNull();
        assertThat(conversor.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void valorDesconhecidoFalhaComMensagemClara() {
        assertThatThrownBy(() -> conversor.convertToEntityAttribute("X"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sexo")
                .hasMessageContaining("X");
    }

    @Test
    void novosEnumsDeTarefasConvertemCorretamente() {
        CategoriaTarefa.Conversor catConv = new CategoriaTarefa.Conversor();
        assertThat(catConv.convertToDatabaseColumn(CategoriaTarefa.CERCA)).isEqualTo("cerca");
        assertThat(catConv.convertToEntityAttribute("cerca")).isEqualTo(CategoriaTarefa.CERCA);

        OrigemTarefa.Conversor origConv = new OrigemTarefa.Conversor();
        assertThat(origConv.convertToDatabaseColumn(OrigemTarefa.OCORRENCIA)).isEqualTo("ocorrencia");
        assertThat(origConv.convertToEntityAttribute("ocorrencia")).isEqualTo(OrigemTarefa.OCORRENCIA);

        StatusTarefa.Conversor statusConv = new StatusTarefa.Conversor();
        assertThat(statusConv.convertToDatabaseColumn(StatusTarefa.EM_ANDAMENTO)).isEqualTo("em_andamento");
        assertThat(statusConv.convertToEntityAttribute("em_andamento")).isEqualTo(StatusTarefa.EM_ANDAMENTO);

        SyncStatus.Conversor syncConv = new SyncStatus.Conversor();
        assertThat(syncConv.convertToDatabaseColumn(SyncStatus.SINCRONIZADO)).isEqualTo("sincronizado");
        assertThat(syncConv.convertToEntityAttribute("sincronizado")).isEqualTo(SyncStatus.SINCRONIZADO);

        TipoEventoTarefa.Conversor tipoConv = new TipoEventoTarefa.Conversor();
        assertThat(tipoConv.convertToDatabaseColumn(TipoEventoTarefa.OBSERVACAO)).isEqualTo("observacao");
        assertThat(tipoConv.convertToEntityAttribute("observacao")).isEqualTo(TipoEventoTarefa.OBSERVACAO);
    }
}
