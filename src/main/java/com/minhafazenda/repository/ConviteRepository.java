package com.minhafazenda.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.minhafazenda.entity.Convite;

public interface ConviteRepository extends JpaRepository<Convite, UUID> {

    /** O lock impede que dois aceites simultâneos consumam o mesmo uso. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Convite c where c.tokenHash = :tokenHash")
    Optional<Convite> buscarPorTokenHashComLock(@Param("tokenHash") String tokenHash);
}
