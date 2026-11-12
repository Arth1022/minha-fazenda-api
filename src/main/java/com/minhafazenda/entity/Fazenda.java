package com.minhafazenda.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "fazendas")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fazenda {

    @Id
    private UUID id;

    @Column(nullable = false, columnDefinition = "text")
    private String nome;

    private UUID createdBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    private Instant deletedAt;

    public Fazenda(UUID id, String nome, UUID createdBy) {
        this.id = id;
        this.nome = nome.strip();
        this.createdBy = createdBy;
    }

    public void renomear(String novoNome) {
        this.nome = novoNome.strip();
    }
}
