package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Tempo e km de um uso de máquina numa área. */
@Getter
@Entity
@Table(name = "usos_area", indexes = @Index(name = "idx_usos_area_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsoArea extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID usoId;

    @Column(nullable = false)
    private UUID areaId;

    @Column(nullable = false)
    private OffsetDateTime inicio;

    @Column(nullable = false)
    private Integer segundos;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal km;
}
