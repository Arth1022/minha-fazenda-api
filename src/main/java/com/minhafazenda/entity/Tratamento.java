package com.minhafazenda.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Tratamento com agenda de doses e carências. */
@Getter
@Entity
@Table(name = "tratamentos", indexes = @Index(name = "idx_tratamentos_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tratamento extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID animalId;

    @Column(nullable = false, columnDefinition = "text")
    private String medicamento;

    @Column(columnDefinition = "text")
    private String dose;

    @Column(nullable = false)
    private OffsetDateTime dataInicio;

    private OffsetDateTime dataFim;

    @Column(nullable = false, columnDefinition = "text")
    private String diasSemana;

    @Column(columnDefinition = "text")
    private String horario;

    private Integer intervaloHoras;

    @Column(nullable = false)
    private Integer avisoMinutosAntes;

    @Column(nullable = false)
    private Boolean notificacaoAtiva;

    private Integer carenciaLeiteDias;

    private Integer carenciaCarneDias;

    @Column(columnDefinition = "text")
    private String observacoes;

    private UUID grupoAplicacaoId;

    @Column(columnDefinition = "text")
    private String comprovanteUri;
}
