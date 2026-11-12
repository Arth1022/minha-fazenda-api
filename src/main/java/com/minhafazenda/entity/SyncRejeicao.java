package com.minhafazenda.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Operação do app que a API recusou, guardada para diagnóstico. */
@Getter
@Entity
@Table(name = "sync_rejeicoes", indexes = @Index(name = "idx_sync_rejeicoes_usuario_id", columnList = "usuario_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SyncRejeicao {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @Column(nullable = false, updatable = false, columnDefinition = "text")
    private String tabela;

    @Column(nullable = false, updatable = false, columnDefinition = "text")
    private String registroId;

    @Column(nullable = false, updatable = false, columnDefinition = "text")
    private String op;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(updatable = false)
    private String dados;

    @Column(nullable = false, updatable = false, columnDefinition = "text")
    private String motivo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public SyncRejeicao(Usuario usuario, String tabela, String registroId, String op, String dados, String motivo) {
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.tabela = tabela;
        this.registroId = registroId;
        this.op = op;
        this.dados = dados;
        this.motivo = motivo;
    }
}
