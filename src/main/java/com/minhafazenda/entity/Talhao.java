package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.minhafazenda.enums.FaseLavoura;
import com.minhafazenda.enums.TipoTalhao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Área do mapa (pasto, lavoura, reserva, outro) com geometria GeoJSON. */
@Getter
@Entity
@Table(name = "talhoes", indexes = @Index(name = "idx_talhoes_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Talhao extends BaseFazendaEntity {

    @Column(nullable = false, columnDefinition = "text")
    private String nome;

    @Column(nullable = false, columnDefinition = "text")
    private TipoTalhao tipo;

    @Column(nullable = false, columnDefinition = "text")
    private String cor;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal areaHectares;

    @Column(columnDefinition = "numeric")
    private BigDecimal perimetroM;

    @Column(nullable = false, columnDefinition = "text")
    private String geometriaJson;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal centroLat;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal centroLng;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal minLat;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal maxLat;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal minLng;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal maxLng;

    @Column(columnDefinition = "text")
    private String cultura;

    @Column(columnDefinition = "text")
    private FaseLavoura faseLavoura;

    private OffsetDateTime dataPlantio;
}
