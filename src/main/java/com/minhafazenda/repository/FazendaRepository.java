package com.minhafazenda.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhafazenda.entity.Fazenda;

public interface FazendaRepository extends JpaRepository<Fazenda, UUID> {

    Optional<Fazenda> findByIdAndDeletedAtIsNull(UUID id);
}
