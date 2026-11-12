package com.minhafazenda.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.ResultadoExame;
import com.minhafazenda.enums.TipoExame;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Exame do animal, com validade. */
@Getter
@Entity
@Table(name = "exames", indexes = @Index(name = "idx_exames_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exame extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID animalId;

    @Column(nullable = false)
    private OffsetDateTime data;

    @Column(nullable = false, columnDefinition = "text")
    private TipoExame tipo;

    @Column(columnDefinition = "text")
    private String descricao;

    @Column(nullable = false, columnDefinition = "text")
    private ResultadoExame resultado;

    private Integer validadeDias;

    @Column(columnDefinition = "text")
    private String comprovanteUri;
}
