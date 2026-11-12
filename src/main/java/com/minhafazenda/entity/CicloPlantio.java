package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.StatusCiclo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Ciclo de plantio de uma lavoura. */
@Getter
@Entity
@Table(name = "ciclos_plantio", indexes = @Index(name = "idx_ciclos_plantio_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CicloPlantio extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID areaId;

    @Column(nullable = false, columnDefinition = "text")
    private String cultura;

    @Column(columnDefinition = "text")
    private String modeloId;

    @Column(nullable = false)
    private OffsetDateTime dataPlantio;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal areaHa;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal taxaKgHa;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal sacoKg;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal sementesKg;

    @Column(nullable = false)
    private Integer sacos;

    @Column(nullable = false)
    private Boolean calculoDesatualizado;

    @Column(nullable = false, columnDefinition = "text")
    private StatusCiclo status;

    private OffsetDateTime encerradoEm;
}
