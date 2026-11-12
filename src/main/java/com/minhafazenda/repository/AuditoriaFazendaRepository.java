package com.minhafazenda.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhafazenda.entity.AuditoriaFazenda;

public interface AuditoriaFazendaRepository extends JpaRepository<AuditoriaFazenda, UUID> {

    List<AuditoriaFazenda> findByFazendaIdOrderByCreatedAt(UUID fazendaId);
}
