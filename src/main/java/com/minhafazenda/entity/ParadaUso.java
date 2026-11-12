package com.minhafazenda.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.MotivoParada;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Pausa ou resposta "Continua" durante um uso. */
@Getter
@Entity
@Table(name = "paradas_uso", indexes = @Index(name = "idx_paradas_uso_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParadaUso extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID usoId;

    @Column(nullable = false, columnDefinition = "text")
    private MotivoParada motivo;

    @Column(nullable = false)
    private OffsetDateTime inicio;

    private OffsetDateTime fim;
}
