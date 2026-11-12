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

/** Lote de animais, com o pasto em que foi colocado inteiro. */
@Getter
@Entity
@Table(name = "lotes", indexes = @Index(name = "idx_lotes_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lote extends BaseFazendaEntity {

    @Column(nullable = false, columnDefinition = "text")
    private String nome;

    @Column(columnDefinition = "text")
    private String descricao;

    private UUID areaId;

    private OffsetDateTime areaDesde;
}
