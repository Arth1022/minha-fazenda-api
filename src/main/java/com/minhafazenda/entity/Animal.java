package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.minhafazenda.enums.OrigemAnimal;
import com.minhafazenda.enums.Sexo;
import com.minhafazenda.enums.StatusAnimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Animal do rebanho. */
@Getter
@Entity
@Table(name = "animais", indexes = @Index(name = "idx_animais_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Animal extends BaseFazendaEntity {

    @Column(nullable = false, columnDefinition = "text")
    private String brinco;

    @Column(columnDefinition = "text")
    private String nome;

    @Column(columnDefinition = "text")
    private String raca;

    @Column(nullable = false, columnDefinition = "text")
    private Sexo sexo;

    private OffsetDateTime dataNascimento;

    @Column(nullable = false)
    private Boolean nascimentoAproximado;

    @Column(nullable = false)
    private Boolean jaPariu;

    @Column(nullable = false)
    private Integer criasAnteriores;

    private UUID maeId;

    private UUID paiId;

    @Column(columnDefinition = "text")
    private String paiDescricao;

    @Column(columnDefinition = "text")
    private OrigemAnimal origem;

    private OffsetDateTime dataEntrada;

    @Column(nullable = false, columnDefinition = "text")
    private StatusAnimal status;

    private OffsetDateTime dataSaida;

    @Column(columnDefinition = "text")
    private String motivoSaida;

    private UUID loteId;

    @Column(name = "producao_leite_l", columnDefinition = "numeric")
    private BigDecimal producaoLeiteL;

    @Column(columnDefinition = "numeric")
    private BigDecimal racaoKgDia;

    @Column(columnDefinition = "numeric")
    private BigDecimal valorCompra;

    @Column(columnDefinition = "numeric")
    private BigDecimal valorVenda;

    @Column(columnDefinition = "numeric")
    private BigDecimal pesoSaidaKg;

    @Column(columnDefinition = "text")
    private String sisbov;

    @Column(columnDefinition = "text")
    private String grauSangue;

    @Column(columnDefinition = "text")
    private String fotoUri;

    private OffsetDateTime dataDesmame;

    @Column(nullable = false)
    private Boolean partoAssistido;
}
