package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.CategoriaOcorrencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Problema relatado na máquina (resolvida_em nulo = em aberto). */
@Getter
@Entity
@Table(name = "ocorrencias_maquina", indexes = @Index(name = "idx_ocorrencias_maquina_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OcorrenciaMaquina extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID maquinaId;

    private UUID usoId;

    @Column(columnDefinition = "text")
    private String operador;

    private UUID operadorId;

    @Column(nullable = false, columnDefinition = "text")
    private CategoriaOcorrencia categoria;

    @Column(nullable = false)
    private Boolean podeUsar;

    @Column(columnDefinition = "text")
    private String descricao;

    @Column(columnDefinition = "text")
    private String fotoUri;

    @Column(columnDefinition = "numeric")
    private BigDecimal lat;

    @Column(columnDefinition = "numeric")
    private BigDecimal lng;

    @Column(nullable = false)
    private OffsetDateTime registradoEm;

    private OffsetDateTime resolvidaEm;
}
