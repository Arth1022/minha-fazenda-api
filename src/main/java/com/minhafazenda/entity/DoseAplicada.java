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

/** Dose confirmada num dia do tratamento. */
@Getter
@Entity
@Table(name = "doses_aplicadas", indexes = @Index(name = "idx_doses_aplicadas_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoseAplicada extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID tratamentoId;

    @Column(nullable = false)
    private OffsetDateTime dia;
}
