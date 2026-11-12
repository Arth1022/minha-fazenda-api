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

@Getter
@Entity
@Table(name = "convites",
        uniqueConstraints = @UniqueConstraint(name = "uk_convites_token_hash", columnNames = "token_hash"),
        indexes = @Index(name = "idx_convites_fazenda_id", columnList = "fazenda_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Convite {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fazenda_id", nullable = false, updatable = false)
    private Fazenda fazenda;

    @Column(nullable = false, updatable = false, columnDefinition = "text")
    private String tokenHash;

    @Column(nullable = false, updatable = false)
    private UUID criadoPor;

    @Column(nullable = false, updatable = false)
    private Instant expiraEm;

    @Column(nullable = false, updatable = false)
    private int usosMax;

    @Column(nullable = false)
    private int usos;

    private Instant revogadoEm;

    /**
     * Código (6 caracteres) que o colaborador digita no app. Só existe no convite por código
     * (vale 24 h): o dono pode ver o código de novo enquanto vale, então ele fica em texto além do hash.
     */
    @Column(updatable = false, columnDefinition = "text")
    private String codigo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Convite(Fazenda fazenda, String tokenHash, UUID criadoPor, Instant expiraEm, int usosMax) {
        this.id = UUID.randomUUID();
        this.fazenda = fazenda;
        this.tokenHash = tokenHash;
        this.criadoPor = criadoPor;
        this.expiraEm = expiraEm;
        this.usosMax = usosMax;
    }

    /** Convite por código: vale até expiraEm e para uma pessoa só. */
    public static Convite codigoDaFazenda(Fazenda fazenda, String codigo, String codigoHash, UUID criadoPor,
            Instant expiraEm) {
        Convite convite = new Convite(fazenda, codigoHash, criadoPor, expiraEm, 1);
        convite.codigo = codigo;
        return convite;
    }

    public boolean estaValido(Instant agora) {
        return revogadoEm == null && agora.isBefore(expiraEm) && usos < usosMax;
    }

    public void consumir() {
        usos++;
    }

    public void revogar(Instant agora) {
        if (revogadoEm == null) {
            revogadoEm = agora;
        }
    }
}
