package com.minhafazenda.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.minhafazenda.entity.FazendaMembro;
import com.minhafazenda.enums.Papel;

public interface FazendaMembroRepository extends JpaRepository<FazendaMembro, UUID> {

    Optional<FazendaMembro> findByFazendaIdAndUsuarioId(UUID fazendaId, UUID usuarioId);

    Optional<FazendaMembro> findByFazendaIdAndUsuarioIdAndRemovedAtIsNull(UUID fazendaId, UUID usuarioId);

    /** O lock serializa mudanças concorrentes de papel, para dois owners não se rebaixarem ao mesmo tempo. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select m from FazendaMembro m
            where m.fazenda.id = :fazendaId and m.papel = :papel and m.removedAt is null
            """)
    List<FazendaMembro> buscarAtivosPorPapelComLock(@Param("fazendaId") UUID fazendaId, @Param("papel") Papel papel);
}
