package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.AtividadeUso;
import com.minhafazenda.enums.MotivoFimUso;
import com.minhafazenda.enums.OrigemUso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Uso de uma máquina (fim nulo = em uso), com o resumo do rastro. */
@Getter
@Entity
@Table(name = "usos_maquina", indexes = @Index(name = "idx_usos_maquina_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsoMaquina extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID maquinaId;

    @Column(nullable = false)
    private OffsetDateTime inicio;

    private OffsetDateTime fim;

    @Column(columnDefinition = "text")
    private String operador;

    private UUID operadorId;

    private UUID implementoId;

    @Column(columnDefinition = "text")
    private AtividadeUso atividade;

    @Column(nullable = false, columnDefinition = "text")
    private OrigemUso origem;

    @Column(columnDefinition = "text")
    private MotivoFimUso fimMotivo;

    @Column(nullable = false)
    private Boolean rastreado;

    @Column(columnDefinition = "numeric")
    private BigDecimal inicioLat;

    @Column(columnDefinition = "numeric")
    private BigDecimal inicioLng;

    @Column(columnDefinition = "numeric")
    private BigDecimal fimLat;

    @Column(columnDefinition = "numeric")
    private BigDecimal fimLng;

    @Column(columnDefinition = "numeric")
    private BigDecimal kmTotal;

    @Column(columnDefinition = "numeric")
    private BigDecimal kmTrabalhando;

    private Integer segTrabalhando;

    private Integer segDeslocando;

    private Integer segParada;

    private Integer segPausa;

    private Integer segSemSinal;

    @Column(columnDefinition = "numeric")
    private BigDecimal areaEstimadaHa;

    @Column(columnDefinition = "numeric")
    private BigDecimal qualidade;

    @Column(nullable = false)
    private Boolean localizacaoSimulada;

    @Column(columnDefinition = "text")
    private String rastro;

    @Column(columnDefinition = "text")
    private String talhoes;

    private OffsetDateTime paradaAvisadaEm;

    private OffsetDateTime continuaAte;

    private OffsetDateTime regrasDesde;
}
