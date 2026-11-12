package com.minhafazenda.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import com.minhafazenda.service.NomeUsuario;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(name = "uk_usuarios_email", columnNames = "email"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {

    @Id
    private UUID id;

    @Column(nullable = false, columnDefinition = "text")
    private String nome;

    @Column(columnDefinition = "text")
    private String email;

    @Column(columnDefinition = "text")
    private String telefone;

    @Column(columnDefinition = "text")
    private String credencialHash;

    @Column(columnDefinition = "text")
    private String avatarUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public Usuario(String nome, String email, String telefone, String credencialHash) {
        this.id = UUID.randomUUID();
        this.nome = NomeUsuario.normalizar(nome);
        this.email = email;
        this.telefone = telefone;
        this.credencialHash = credencialHash;
    }

    public void renomear(String novoNome) {
        this.nome = NomeUsuario.normalizar(novoNome);
    }

    public void definirAvatar(String url) {
        this.avatarUrl = url;
    }

    public void removerAvatar() {
        this.avatarUrl = null;
    }
}
