package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.MetodoPesagem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Pesagem do animal (balança ou fita). */
@Getter
@Entity
@Table(name = "pesagens", indexes = @Index(name = "idx_pesagens_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pesagem extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID animalId;

    @Column(nullable = false)
    private OffsetDateTime data;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal pesoKg;

    @Column(nullable = false, columnDefinition = "text")
    private MetodoPesagem metodo;
}
