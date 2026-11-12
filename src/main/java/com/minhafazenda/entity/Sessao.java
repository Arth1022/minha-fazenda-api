package com.minhafazenda.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Um aparelho logado. Do refresh token só o SHA-256 é guardado. */
@Getter
@Entity
@Table(name = "sessoes",
        uniqueConstraints = @UniqueConstraint(name = "uk_sessoes_refresh_hash", columnNames = "refresh_hash"),
        indexes = {
            @Index(name = "idx_sessoes_usuario_id", columnList = "usuario_id"),
            @Index(name = "idx_sessoes_refresh_hash_anterior", columnList = "refresh_hash_anterior")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sessao {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @Column(nullable = false, updatable = false, columnDefinition = "text")
    private String deviceId;

    @Column(nullable = false, columnDefinition = "text")
    private String refreshHash;

    /** Hash do refresh trocado na última rotação; se ele reaparecer, o token foi copiado. */
    @Column(columnDefinition = "text")
    private String refreshHashAnterior;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant revokedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Sessao(Usuario usuario, String deviceId, String refreshHash, Instant expiresAt) {
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.deviceId = deviceId;
        this.refreshHash = refreshHash;
        this.expiresAt = expiresAt;
    }

    public boolean estaAtiva(Instant agora) {
        return revokedAt == null && agora.isBefore(expiresAt);
    }

    public boolean pertenceAoAparelho(String deviceId) {
        return this.deviceId.equals(deviceId);
    }

    public void rotacionar(String novoRefreshHash, Instant novaExpiracao) {
        this.refreshHashAnterior = this.refreshHash;
        this.refreshHash = novoRefreshHash;
        this.expiresAt = novaExpiracao;
    }

    public void revogar(Instant agora) {
        if (revokedAt == null) {
            revokedAt = agora;
        }
    }
}
