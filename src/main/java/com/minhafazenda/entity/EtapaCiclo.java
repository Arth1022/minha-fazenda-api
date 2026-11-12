package com.minhafazenda.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.TipoEtapa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Etapa da linha do tempo do ciclo; com responsável, vira tarefa. */
@Getter
@Entity
@Table(name = "etapas_ciclo", indexes = @Index(name = "idx_etapas_ciclo_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EtapaCiclo extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID cicloId;

    @Column(nullable = false)
    private Integer ordem;

    @Column(nullable = false, columnDefinition = "text")
    private String nome;

    @Column(nullable = false, columnDefinition = "text")
    private TipoEtapa tipo;

    @Column(nullable = false)
    private OffsetDateTime previstaEm;

    private OffsetDateTime realizadaEm;

    @Column(columnDefinition = "text")
    private String observacao;

    private UUID responsavelId;

    @Column(columnDefinition = "text")
    private String responsavelNome;

    private OffsetDateTime atribuidaEm;

    private UUID atribuidaPor;
}
