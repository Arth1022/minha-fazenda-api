package com.minhafazenda.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.MetodoDiagnostico;
import com.minhafazenda.enums.TipoReproducao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Cobertura ou inseminação, com diagnóstico, parto e aborto. */
@Getter
@Entity
@Table(name = "reproducoes", indexes = @Index(name = "idx_reproducoes_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reproducao extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID animalId;

    @Column(nullable = false, columnDefinition = "text")
    private TipoReproducao tipo;

    @Column(nullable = false)
    private OffsetDateTime data;

    private Boolean prenhezConfirmada;

    private OffsetDateTime previsaoParto;

    private OffsetDateTime dataParto;

    private UUID criaId;

    private UUID touroId;

    @Column(columnDefinition = "text")
    private String touroDescricao;

    private OffsetDateTime dataDiagnostico;

    @Column(columnDefinition = "text")
    private MetodoDiagnostico metodoDiagnostico;

    private OffsetDateTime dataAborto;
}
