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

/** Cio observado. */
@Getter
@Entity
@Table(name = "cios", indexes = @Index(name = "idx_cios_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cio extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID animalId;

    @Column(nullable = false)
    private OffsetDateTime data;

    @Column(columnDefinition = "text")
    private String observacoes;
}
