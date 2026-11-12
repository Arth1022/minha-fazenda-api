package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.OrigemLeitura;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Leitura digitada do horímetro. */
@Getter
@Entity
@Table(name = "leituras_horimetro", indexes = @Index(name = "idx_leituras_horimetro_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeituraHorimetro extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID maquinaId;

    private UUID usoId;

    @Column(columnDefinition = "text")
    private String operador;

    private UUID operadorId;

    @Column(nullable = false, columnDefinition = "numeric")
    private BigDecimal valorH;

    @Column(columnDefinition = "numeric")
    private BigDecimal estimadoH;

    @Column(nullable = false, columnDefinition = "text")
    private OrigemLeitura origem;

    @Column(columnDefinition = "text")
    private String fotoUri;

    @Column(nullable = false)
    private OffsetDateTime lidoEm;
}
