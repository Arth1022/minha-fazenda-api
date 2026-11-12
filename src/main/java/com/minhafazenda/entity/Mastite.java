package com.minhafazenda.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.TipoMastite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Caso de mastite com os tetos afetados. */
@Getter
@Entity
@Table(name = "mastites", indexes = @Index(name = "idx_mastites_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mastite extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID animalId;

    @Column(nullable = false)
    private OffsetDateTime data;

    @Column(nullable = false, columnDefinition = "text")
    private String tetos;

    @Column(nullable = false, columnDefinition = "text")
    private TipoMastite tipo;

    @Column(columnDefinition = "text")
    private String observacoes;
}
