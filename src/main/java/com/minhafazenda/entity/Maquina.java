package com.minhafazenda.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.minhafazenda.enums.CategoriaMaquina;
import com.minhafazenda.enums.Combustivel;
import com.minhafazenda.enums.StatusMaquina;
import com.minhafazenda.enums.TipoMaquina;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Máquina ou implemento; codigo é o ID curto impresso no QR. */
@Getter
@Entity
@Table(name = "maquinas", indexes = @Index(name = "idx_maquinas_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Maquina extends BaseFazendaEntity {

    @Column(nullable = false, columnDefinition = "text")
    private String codigo;

    @Column(nullable = false, columnDefinition = "text")
    private String nome;

    @Column(nullable = false, columnDefinition = "text")
    private TipoMaquina tipo;

    @Column(nullable = false, columnDefinition = "text")
    private CategoriaMaquina categoria;

    @Column(columnDefinition = "text")
    private String marca;

    @Column(columnDefinition = "text")
    private String modelo;

    private Integer anoFabricacao;

    @Column(columnDefinition = "text")
    private String numeroSerie;

    @Column(columnDefinition = "text")
    private String placa;

    @Column(columnDefinition = "numeric")
    private BigDecimal potenciaCv;

    @Column(columnDefinition = "text")
    private Combustivel combustivel;

    @Column(columnDefinition = "numeric")
    private BigDecimal tanqueLitros;

    @Column(columnDefinition = "numeric")
    private BigDecimal larguraTrabalhoM;

    @Column(nullable = false)
    private Boolean possuiHorimetro;

    @Column(columnDefinition = "numeric")
    private BigDecimal velocidadeMaxKmh;

    @Column(columnDefinition = "numeric")
    private BigDecimal horimetroInicial;

    private OffsetDateTime dataAquisicao;

    @Column(nullable = false, columnDefinition = "text")
    private StatusMaquina status;
}
