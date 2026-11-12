package com.minhafazenda.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/**
 * Colunas comuns a toda tabela de domínio. As linhas são escritas pelo sync (JdbcTemplate);
 * a Entity existe para o Liquibase gerar o schema, por isso não tem setters.
 */
@Getter
@MappedSuperclass
public abstract class BaseFazendaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fazenda_id", nullable = false, updatable = false)
    private Fazenda fazenda;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "server_updated_at")
    private OffsetDateTime serverUpdatedAt;
}
