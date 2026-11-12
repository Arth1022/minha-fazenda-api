package com.minhafazenda.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.minhafazenda.enums.Papel;

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

@Getter
@Entity
@Table(name = "fazenda_membros",
        uniqueConstraints = @UniqueConstraint(name = "uk_fazenda_membros_fazenda_usuario",
                columnNames = {"fazenda_id", "usuario_id"}),
        indexes = @Index(name = "idx_fazenda_membros_usuario_id", columnList = "usuario_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FazendaMembro {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fazenda_id", nullable = false, updatable = false)
    private Fazenda fazenda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "text")
    private Papel papel;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant removedAt;

    public FazendaMembro(Fazenda fazenda, Usuario usuario, Papel papel) {
        this.id = UUID.randomUUID();
        this.fazenda = fazenda;
        this.usuario = usuario;
        this.papel = papel;
    }

    public boolean ehOwner() {
        return papel == Papel.OWNER;
    }

    public void alterarPapel(Papel novoPapel) {
        this.papel = novoPapel;
    }

    /** Remover nunca apaga a linha: o histórico de quem já esteve na fazenda é preservado. */
    public void remover(Instant agora) {
        this.removedAt = agora;
    }

    public void reativarComo(Papel novoPapel) {
        this.removedAt = null;
        this.papel = novoPapel;
    }
}
