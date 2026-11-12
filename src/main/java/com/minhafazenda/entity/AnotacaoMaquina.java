package com.minhafazenda.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Anotação livre da máquina. */
@Getter
@Entity
@Table(name = "anotacoes_maquina", indexes = @Index(name = "idx_anotacoes_maquina_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnotacaoMaquina extends BaseFazendaEntity {

    @Column(nullable = false)
    private UUID maquinaId;

    private UUID usoId;

    @Column(nullable = false, columnDefinition = "text")
    private String texto;
}
