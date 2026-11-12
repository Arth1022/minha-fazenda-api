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

/** Litros da vaca num dia. */
@Getter
@Entity
@Table(name = "controles_leiteiros", indexes = @Index(name = "idx_controles_leiteiros_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ControleLeiteiro extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID animalId;

    @Column(nullable = false)
    private OffsetDateTime data;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal litros;
}
