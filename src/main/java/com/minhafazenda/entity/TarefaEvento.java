package com.minhafazenda.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.TipoEventoTarefa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Histórico append-only de eventos de uma tarefa. */
@Getter
@Entity
@Table(name = "tarefa_eventos", indexes = {
    @Index(name = "tarefa_eventos_fazenda_idx", columnList = "fazenda_id"),
    @Index(name = "tarefa_eventos_tarefa_ocorrido_idx", columnList = "tarefa_id, ocorrido_em")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TarefaEvento extends BaseFazendaEntity {

    private UUID tarefaId;

    @Column(columnDefinition = "text")
    private TipoEventoTarefa tipo;

    private UUID autorId;

    private OffsetDateTime ocorridoEm;

    @Column(columnDefinition = "text")
    private String texto;

    /** JSON em texto, como as outras colunas JSON do sync (o app grava e lê como string). */
    @Column(columnDefinition = "text")
    private String dados;
}
