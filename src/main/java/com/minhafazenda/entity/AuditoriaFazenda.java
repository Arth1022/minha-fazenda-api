package com.minhafazenda.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.minhafazenda.enums.AcaoAuditoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Quem fez o quê com pessoas e convites da fazenda. O alvo é o usuário afetado ou o convite. */
@Getter
@Entity
@Table(name = "auditoria_fazenda",
        indexes = @Index(name = "idx_auditoria_fazenda_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditoriaFazenda {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fazenda_id", nullable = false, updatable = false)
    private Fazenda fazenda;

    @Column(nullable = false, updatable = false)
    private UUID atorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, columnDefinition = "text")
    private AcaoAuditoria acao;

    @Column(updatable = false)
    private UUID alvoId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public AuditoriaFazenda(Fazenda fazenda, UUID atorId, AcaoAuditoria acao, UUID alvoId) {
        this.id = UUID.randomUUID();
        this.fazenda = fazenda;
        this.atorId = atorId;
        this.acao = acao;
        this.alvoId = alvoId;
    }
}
