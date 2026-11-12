package com.minhafazenda.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Entrada e saída de animal num pasto (saida_em nulo = local atual). */
@Getter
@Entity
@Table(name = "movimentacoes", indexes = @Index(name = "idx_movimentacoes_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movimentacao extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID animalId;

    @Column(nullable = false)
    private UUID areaId;

    private UUID loteId;

    @Column(nullable = false)
    private OffsetDateTime entradaEm;

    private OffsetDateTime saidaEm;

    @Column(columnDefinition = "text")
    private String registradoPor;

    private UUID registradoPorId;
}
