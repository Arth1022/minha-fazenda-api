package com.minhafazenda.service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhafazenda.config.JwtProperties;
import com.minhafazenda.dto.SessaoAberta;
import com.minhafazenda.entity.Sessao;
import com.minhafazenda.entity.Usuario;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.repository.SessaoRepository;
import com.minhafazenda.security.SessoesRevogadas;
import com.minhafazenda.security.TokenSeguro;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessaoService {

    private final SessaoRepository sessoes;
    private final SessoesRevogadas sessoesRevogadas;
    private final JwtService jwtService;
    private final JwtProperties properties;
    private final Clock relogio;

    @Transactional
    public SessaoAberta abrir(Usuario usuario, String deviceId) {
        String refreshToken = TokenSeguro.gerar();
        Sessao sessao = sessoes.save(new Sessao(usuario, deviceId, TokenSeguro.hash(refreshToken),
                novaExpiracao(relogio.instant())));
        return new SessaoAberta(jwtService.emitirAccessToken(usuario, sessao.getId()), refreshToken);
    }

    /**
     * Troca o refresh por um par novo. Um refresh já trocado que reaparece indica cópia do token,
     * então a sessão inteira é revogada.
     */
    // noRollbackFor: a revogação precisa ser gravada mesmo com a requisição terminando em erro.
    @Transactional(noRollbackFor = ApiException.class)
    public SessaoAberta renovar(String refreshToken, String deviceId) {
        Instant agora = relogio.instant();
        String hash = TokenSeguro.hash(refreshToken);
        Sessao sessao = sessoes.findByRefreshHash(hash).orElseThrow(() -> aoNaoReconhecer(hash, agora));
        if (!sessao.estaAtiva(agora)) {
            throw new ApiException(CodigoErro.SESSAO_ENCERRADA);
        }
        if (!sessao.pertenceAoAparelho(deviceId)) {
            revogar(sessao, agora);
            throw new ApiException(CodigoErro.SESSAO_ENCERRADA);
        }
        String novoRefreshToken = TokenSeguro.gerar();
        sessao.rotacionar(TokenSeguro.hash(novoRefreshToken), novaExpiracao(agora));
        return new SessaoAberta(jwtService.emitirAccessToken(sessao.getUsuario(), sessao.getId()), novoRefreshToken);
    }

    @Transactional
    public void encerrar(UUID usuarioId, String refreshToken) {
        sessoes.findByRefreshHash(TokenSeguro.hash(refreshToken))
                .filter(sessao -> sessao.getUsuario().getId().equals(usuarioId))
                .ifPresent(sessao -> revogar(sessao, relogio.instant()));
    }

    private ApiException aoNaoReconhecer(String hash, Instant agora) {
        sessoes.findByRefreshHashAnterior(hash).ifPresent(sessao -> revogar(sessao, agora));
        return new ApiException(CodigoErro.SESSAO_ENCERRADA);
    }

    private void revogar(Sessao sessao, Instant agora) {
        sessao.revogar(agora);
        sessoesRevogadas.marcar(sessao.getId());
    }

    private Instant novaExpiracao(Instant agora) {
        return agora.plus(properties.validadeRefresh());
    }
}
