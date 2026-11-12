package com.minhafazenda.security;

import java.util.UUID;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/** Recusa access tokens sem sessão (como o token do PowerSync) ou de sessão revogada. */
@Component
@RequiredArgsConstructor
public class SessaoAtivaValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error SESSAO_INVALIDA =
            new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Sessão ausente ou revogada", null);

    private final SessoesRevogadas sessoesRevogadas;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String sessaoId = jwt.getClaimAsString(UsuarioAutenticadoResolver.CLAIM_SESSAO);
        if (sessaoId == null || sessoesRevogadas.contem(UUID.fromString(sessaoId))) {
            return OAuth2TokenValidatorResult.failure(SESSAO_INVALIDA);
        }
        return OAuth2TokenValidatorResult.success();
    }
}
