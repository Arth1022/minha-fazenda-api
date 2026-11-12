package com.minhafazenda.dto;

import java.util.UUID;

import com.minhafazenda.entity.Fazenda;

public record FazendaResponse(UUID id, String nome) {

    public static FazendaResponse de(Fazenda fazenda) {
        return new FazendaResponse(fazenda.getId(), fazenda.getNome());
    }
}
