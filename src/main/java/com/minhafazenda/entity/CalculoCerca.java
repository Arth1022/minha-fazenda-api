package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.minhafazenda.enums.TipoCerca;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Conta de cerca salva para uma área. */
@Getter
@Entity
@Table(name = "calculos_cerca", indexes = @Index(name = "idx_calculos_cerca_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalculoCerca extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID areaId;

    @Column(nullable = false, columnDefinition = "text")
    private TipoCerca tipoCerca;

    @Column(nullable = false)
    private Integer fios;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal espacamentoM;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal perimetroM;

    @Column(nullable = false)
    private Integer cantos;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal metrosArame;

    @Column(nullable = false)
    private Integer rolos;

    @Column(nullable = false)
    private Integer mouroes;

    @Column(nullable = false)
    private Integer esticadores;

    @Column(columnDefinition = "numeric")
    private BigDecimal precoRolo;

    @Column(columnDefinition = "numeric")
    private BigDecimal precoMourao;

    @Column(columnDefinition = "numeric")
    private BigDecimal precoEsticador;

    @Column(columnDefinition = "numeric")
    private BigDecimal custo;

    @Column(nullable = false)
    private Boolean desatualizado;
}
