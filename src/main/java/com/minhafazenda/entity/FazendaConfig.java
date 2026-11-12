package com.minhafazenda.entity;

import java.math.BigDecimal;

import com.minhafazenda.enums.TipoProducao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Configuração da fazenda: uma linha por fazenda, com id igual ao fazenda_id. */
@Getter
@Entity
@Table(name = "fazenda_config", indexes = @Index(name = "idx_fazenda_config_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FazendaConfig extends BaseFazendaEntity {

    @Column(nullable = false, columnDefinition = "text")
    private TipoProducao tipoProducao;

    @Column(columnDefinition = "text")
    private String divisaJson;

    @Column(columnDefinition = "numeric")
    private BigDecimal areaHectares;

    @Column(columnDefinition = "numeric")
    private BigDecimal minLat;

    @Column(columnDefinition = "numeric")
    private BigDecimal maxLat;

    @Column(columnDefinition = "numeric")
    private BigDecimal minLng;

    @Column(columnDefinition = "numeric")
    private BigDecimal maxLng;
}
