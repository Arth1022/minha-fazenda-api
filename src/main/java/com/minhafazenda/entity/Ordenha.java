package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.minhafazenda.enums.PeriodoOrdenha;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Horário e registro de ordenha por turno. */
@Getter
@Entity
@Table(name = "ordenhas", indexes = @Index(name = "idx_ordenhas_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ordenha extends BaseFazendaEntity {

    @Column(nullable = false, columnDefinition = "text")
    private PeriodoOrdenha periodo;

    @Column(columnDefinition = "text")
    private String horarioPrevisto;

    @Column(columnDefinition = "text")
    private String horarioNotificacao;

    @Column(columnDefinition = "numeric")
    private BigDecimal volumeLitros;

    @Column(nullable = false)
    private OffsetDateTime dataRegistro;
}
