package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.CategoriaTarefa;
import com.minhafazenda.enums.OrigemTarefa;
import com.minhafazenda.enums.StatusTarefa;
import com.minhafazenda.enums.SyncStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Ocorrência ou tarefa da fazenda. */
@Getter
@Entity
@Table(name = "tarefas", indexes = {
    @Index(name = "tarefas_fazenda_idx", columnList = "fazenda_id"),
    @Index(name = "tarefas_status_responsavel_urgente_idx", columnList = "status, responsavel_id, urgente"),
    @Index(name = "tarefas_responsavel_urgente_prazo_idx", columnList = "responsavel_id, urgente, prazo"),
    @Index(name = "tarefas_animal_id_idx", columnList = "animal_id"),
    @Index(name = "tarefas_maquina_id_idx", columnList = "maquina_id"),
    @Index(name = "tarefas_area_id_idx", columnList = "area_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tarefa extends BaseFazendaEntity {

    @Column(columnDefinition = "text")
    private String titulo;

    @Column(columnDefinition = "text")
    private CategoriaTarefa categoria;

    @Column(columnDefinition = "text")
    private OrigemTarefa origem;

    @Column(columnDefinition = "text")
    private String descricao;

    private Boolean urgente;

    @Column(columnDefinition = "text")
    private StatusTarefa status;

    private UUID responsavelId;

    private OffsetDateTime prazo;

    private UUID areaId;

    private UUID animalId;

    private UUID maquinaId;

    private Boolean maquinaParada;

    @Column(columnDefinition = "text")
    private String audioUri;

    @Column(columnDefinition = "numeric")
    private BigDecimal pontoLat;

    @Column(columnDefinition = "numeric")
    private BigDecimal pontoLng;

    @Column(columnDefinition = "text")
    private SyncStatus syncStatus;

    private OffsetDateTime concluidaEm;
}
