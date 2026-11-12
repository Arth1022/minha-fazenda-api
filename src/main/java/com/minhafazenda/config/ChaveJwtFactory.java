package com.minhafazenda.config;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import org.springframework.util.StringUtils;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

import lombok.extern.slf4j.Slf4j;

/** Monta o par RSA que assina os tokens da API e o token do PowerSync. */
@Slf4j
final class ChaveJwtFactory {

    private static final int TAMANHO_DA_CHAVE_EFEMERA = 2048;

    private ChaveJwtFactory() {
    }

    static RSAKey criar(JwtProperties properties) {
        try {
            if (StringUtils.hasText(properties.privateKey())) {
                return dePem(properties.privateKey(), properties.kid());
            }
            if (properties.permitirChaveEfemera()) {
                log.warn("JWT_PRIVATE_KEY ausente: usando chave RSA efêmera; os tokens deixam de valer ao reiniciar.");
                return efemera(properties.kid());
            }
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalStateException("JWT_PRIVATE_KEY inválida: esperado RSA em PEM PKCS#8", e);
        }
        throw new IllegalStateException("JWT_PRIVATE_KEY é obrigatória neste perfil");
    }

    private static RSAKey dePem(String pem, String kid) throws GeneralSecurityException {
        String base64 = pem
                .replaceAll("-----[A-Z ]+-----", "")
                .replace("\\n", "")
                .replaceAll("\\s", "");
        KeyFactory fabrica = KeyFactory.getInstance("RSA");
        RSAPrivateCrtKey privada = (RSAPrivateCrtKey) fabrica.generatePrivate(
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64)));
        RSAPublicKey publica = (RSAPublicKey) fabrica.generatePublic(
                new RSAPublicKeySpec(privada.getModulus(), privada.getPublicExponent()));
        return montar(publica, privada, kid);
    }

    private static RSAKey efemera(String kid) throws GeneralSecurityException {
        KeyPairGenerator gerador = KeyPairGenerator.getInstance("RSA");
        gerador.initialize(TAMANHO_DA_CHAVE_EFEMERA);
        KeyPair par = gerador.generateKeyPair();
        return montar((RSAPublicKey) par.getPublic(), (RSAPrivateKey) par.getPrivate(), kid);
    }

    private static RSAKey montar(RSAPublicKey publica, RSAPrivateKey privada, String kid) {
        return new RSAKey.Builder(publica)
                .privateKey(privada)
                .keyID(kid)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .build();
    }
}
