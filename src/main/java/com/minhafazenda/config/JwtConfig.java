package com.minhafazenda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.minhafazenda.security.SessaoAtivaValidator;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

@Configuration
public class JwtConfig {

    @Bean
    RSAKey chaveJwt(JwtProperties properties) {
        return ChaveJwtFactory.criar(properties);
    }

    @Bean
    JwtEncoder jwtEncoder(RSAKey chaveJwt) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(chaveJwt)));
    }

    @Bean
    JwtDecoder jwtDecoder(RSAKey chaveJwt, SessaoAtivaValidator sessaoAtiva) throws JOSEException {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(chaveJwt.toRSAPublicKey())
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefault(), sessaoAtiva));
        return decoder;
    }
}
