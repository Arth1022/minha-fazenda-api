package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.minhafazenda.enums.TipoMarcador;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Ponto de atenção no mapa (praga, daninha, erosão). */
@Getter
@Entity
@Table(name = "marcadores_mapa", indexes = @Index(name = "idx_marcadores_mapa_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarcadorMapa extends BaseFazendaEntity {

    private UUID talhaoId;

    @Column(nullable = false, columnDefinition = "text")
    private TipoMarcador tipo;

    @Column(nullable = false, columnDefinition = "text")
    private String titulo;

    @Column(columnDefinition = "text")
    private String descricao;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal lat;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal lng;

    @Column(nullable = false)
    private Boolean resolvido;
}
