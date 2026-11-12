package com.minhafazenda.controller;

import static com.minhafazenda.apoio.Respostas.erro;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import com.minhafazenda.apoio.Conta;
import com.minhafazenda.apoio.IntegracaoTest;
import com.minhafazenda.apoio.Respostas;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import tools.jackson.databind.JsonNode;

class JwtIntegrationTest extends IntegracaoTest {

    @Value("${powersync.url}")
    private String powerSyncUrl;

    @Test
    void jwksEPublicaENaoExpoeAChavePrivada() throws Exception {
        RSAKey chave = chavePublicada("dev");

        assertThat(chave.isPrivate()).isFalse();
        assertThat(chave.getAlgorithm()).isEqualTo(JWSAlgorithm.RS256);
    }

    @Test
    void accessTokenLevaSubNomeSessaoEValeTrintaMinutos() throws Exception {
        Conta conta = api.registrarComFazenda();

        SignedJWT jwt = SignedJWT.parse(conta.accessToken());
        JWTClaimsSet claims = jwt.getJWTClaimsSet();

        assertThat(jwt.getHeader().getAlgorithm()).isEqualTo(JWSAlgorithm.RS256);
        assertThat(jwt.verify(new RSASSAVerifier(chavePublicada(jwt.getHeader().getKeyID())))).isTrue();
        assertThat(claims.getSubject()).isEqualTo(conta.usuarioId().toString());
        assertThat(claims.getStringClaim("nome")).isEqualTo("Pessoa de Teste");
        assertThat(claims.getStringClaim("sid")).isNotBlank();
        assertThat(validade(claims)).isEqualTo(Duration.ofMinutes(30));
        assertThat(claims.getClaims()).doesNotContainKeys("fazenda", "fazendaId", "papel");
    }

    @Test
    void tokenDoPowerSyncEValidoContraAJwks() throws Exception {
        Conta conta = api.registrarComFazenda();

        JsonNode resposta = api.json(api.get("/auth/powersync-token", conta).andExpect(status().isOk()));
        SignedJWT jwt = SignedJWT.parse(resposta.get("token").asString());
        JWTClaimsSet claims = jwt.getJWTClaimsSet();

        assertThat(jwt.verify(new RSASSAVerifier(chavePublicada(jwt.getHeader().getKeyID())))).isTrue();
        assertThat(claims.getSubject()).isEqualTo(conta.usuarioId().toString());
        assertThat(claims.getAudience()).containsExactly(powerSyncUrl);
        assertThat(validade(claims)).isEqualTo(Duration.ofMinutes(60));
        assertThat(resposta.get("endpoint").asString()).isEqualTo(powerSyncUrl);
        assertThat(Instant.parse(resposta.get("expiresAt").asString()).getEpochSecond())
                .isEqualTo(claims.getExpirationTime().toInstant().getEpochSecond());
    }

    @Test
    void tokenDoPowerSyncNaoServeComoAccessToken() throws Exception {
        Conta conta = api.registrarComFazenda();
        String tokenPowerSync = api.json(api.get("/auth/powersync-token", conta)).get("token").asString();

        api.getComToken("/usuarios/me", tokenPowerSync).andExpect(erro(401, "TOKEN_INVALIDO"));
    }

    private RSAKey chavePublicada(String kid) throws Exception {
        String jwks = api.getAnonimo("/.well-known/jwks.json").andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JWKSet.parse(jwks).getKeyByKeyId(kid).toRSAKey();
    }

    private static Duration validade(JWTClaimsSet claims) {
        return Duration.between(claims.getIssueTime().toInstant(), claims.getExpirationTime().toInstant());
    }
}
