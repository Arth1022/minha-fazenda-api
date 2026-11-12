package com.minhafazenda.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhafazenda.entity.SyncRejeicao;

public interface SyncRejeicaoRepository extends JpaRepository<SyncRejeicao, UUID> {

    List<SyncRejeicao> findByUsuarioId(UUID usuarioId);
}
