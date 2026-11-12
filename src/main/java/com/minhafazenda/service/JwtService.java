package com.minhafazenda.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.minhafazenda.config.JwtProperties;
import com.minhafazenda.config.PowerSyncProperties;
import com.minhafazenda.dto.AccessToken;
import com.minhafazenda.dto.TokenPowerSync;
import com.minhafazenda.entity.Usuario;
import com.minhafazenda.security.UsuarioAutenticadoResolver;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String CLAIM_NOME = "nome";

    private final JwtEncoder encoder;
    private final RSAKey chaveJwt;
    private final JwtProperties properties;
    private final PowerSyncProperties powerSync;
    private final Clock relogio;

    public AccessToken emitirAccessToken(Usuario usuario, UUID sessaoId) {
        Instant agora = relogio.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuario.getId().toString())
                .claim(CLAIM_NOME, usuario.getNome())
                .claim(UsuarioAutenticadoResolver.CLAIM_SESSAO, sessaoId.toString())
                .issuedAt(agora)
                .expiresAt(agora.plus(properties.validadeAccess()))
                .build();
        return new AccessToken(assinar(claims), properties.validadeAccess().toSeconds());
    }

    /**
     * O token do PowerSync não leva o claim de sessão, então nunca é aceito como access token
     * da API, mesmo assinado pela mesma chave.
     */
    public TokenPowerSync emitirTokenPowerSync(UUID usuarioId) {
        Instant agora = relogio.instant();
        Instant expiraEm = agora.plus(properties.validadePowerSync());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuarioId.toString())
                .audience(List.of(powerSync.url()))
                .issuedAt(agora)
                .expiresAt(expiraEm)
                .build();
        return new TokenPowerSync(assinar(claims), expiraEm);
    }

    /** Só a parte pública da chave; é daqui que o PowerSync valida os tokens. */
    public Map<String, Object> jwks() {
        return new JWKSet(chaveJwt).toJSONObject();
    }

    private String assinar(JwtClaimsSet claims) {
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).keyId(chaveJwt.getKeyID()).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
