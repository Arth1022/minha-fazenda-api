package com.minhafazenda.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import com.minhafazenda.entity.Sessao;

public interface SessaoRepository extends JpaRepository<Sessao, UUID> {

    /** O lock impede que duas renovações simultâneas rotacionem o mesmo refresh token. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Sessao> findByRefreshHash(String refreshHash);

    Optional<Sessao> findByRefreshHashAnterior(String refreshHashAnterior);

    boolean existsByIdAndRevokedAtIsNull(UUID id);
}
