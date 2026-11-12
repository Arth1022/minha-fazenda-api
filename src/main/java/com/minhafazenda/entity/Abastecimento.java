package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.OrigemAbastecimento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Abastecimento de máquina. */
@Getter
@Entity
@Table(name = "abastecimentos", indexes = @Index(name = "idx_abastecimentos_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Abastecimento extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID maquinaId;

    private UUID usoId;

    @Column(columnDefinition = "text")
    private String operador;

    private UUID operadorId;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal litros;

    @Column(nullable = false)
    private Boolean tanqueCheio;

    @Column(nullable = false, columnDefinition = "text")
    private OrigemAbastecimento origem;

    @Column(columnDefinition = "numeric")
    private BigDecimal horimetroH;

    @Column(columnDefinition = "numeric")
    private BigDecimal lat;

    @Column(columnDefinition = "numeric")
    private BigDecimal lng;

    @Column(columnDefinition = "text")
    private String fotoUri;

    @Column(nullable = false)
    private OffsetDateTime abastecidoEm;
}
