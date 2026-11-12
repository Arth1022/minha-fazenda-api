package com.minhafazenda.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Modelo de cultura criado na fazenda. */
@Getter
@Entity
@Table(name = "modelos_cultura", indexes = @Index(name = "idx_modelos_cultura_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModeloCultura extends BaseFazendaEntity {

    @Column(nullable = false, columnDefinition = "text")
    private String nome;

    @Column(columnDefinition = "numeric")
    private BigDecimal taxaKgHa;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal sacoKg;

    @Column(nullable = false, columnDefinition = "text")
    private String etapas;
}
