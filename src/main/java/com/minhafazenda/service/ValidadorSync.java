package com.minhafazenda.service;

import com.minhafazenda.dto.SyncOp;

/** Regra de negócio de uma tabela sincronizada; registre um bean por tabela. */
public interface ValidadorSync {

    String tabela();

    /** Lança {@link OpRejeitadaException} quando a operação viola a regra. */
    void validar(SyncOp op);
}
