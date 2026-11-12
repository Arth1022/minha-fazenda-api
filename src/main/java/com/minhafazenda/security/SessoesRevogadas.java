package com.minhafazenda.security;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.minhafazenda.config.JwtProperties;
import com.minhafazenda.repository.SessaoRepository;

/**
 * Cache curto do estado das sessões, para o access token de uma sessão revogada ser recusado
 * sem consultar o banco em toda requisição. Uma revogação feita em outra instância da API
 * leva no máximo a validade do cache para valer aqui.
 */
@Component
public class SessoesRevogadas {

    private final SessaoRepository sessoes;
    private final Cache<UUID, Boolean> revogadaPorSessao;

    SessoesRevogadas(SessaoRepository sessoes, JwtProperties properties) {
        this.sessoes = sessoes;
        this.revogadaPorSessao = Caffeine.newBuilder()
                .expireAfterWrite(properties.validadeCacheSessao())
                .maximumSize(100_000)
                .build();
    }

    public boolean contem(UUID sessaoId) {
        return revogadaPorSessao.get(sessaoId, id -> !sessoes.existsByIdAndRevokedAtIsNull(id));
    }

    public void marcar(UUID sessaoId) {
        revogadaPorSessao.put(sessaoId, true);
    }
}
