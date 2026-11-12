package com.minhafazenda.security;

import java.io.IOException;
import java.time.Duration;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.minhafazenda.config.RateLimitProperties;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.exception.ErroResponseWriter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/** Limita por IP as rotas que aceitam credenciais ou tokens de convite. Roda antes da cadeia do Spring Security. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String PREFIXO_AUTH = "/auth/";
    private static final String ROTA_ACEITAR_CONVITE = "/convites/aceitar";
    private static final Duration JANELA = Duration.ofMinutes(1);

    private final RateLimitProperties properties;
    private final ErroResponseWriter erroResponseWriter;

    private final Cache<String, Bucket> bucketsPorIp = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10))
            .maximumSize(100_000)
            .build();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String rota = request.getRequestURI();
        return !rota.startsWith(PREFIXO_AUTH) && !rota.equals(ROTA_ACEITAR_CONVITE);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Bucket bucket = bucketsPorIp.get(request.getRemoteAddr(), ip -> novoBucket());
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
            return;
        }
        erroResponseWriter.escrever(response, CodigoErro.MUITAS_TENTATIVAS);
    }

    private Bucket novoBucket() {
        int limite = properties.requisicoesPorMinuto();
        return Bucket.builder()
                .addLimit(banda -> banda.capacity(limite).refillGreedy(limite, JANELA))
                .build();
    }
}
