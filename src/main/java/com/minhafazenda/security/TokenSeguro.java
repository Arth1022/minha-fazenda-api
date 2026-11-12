package com.minhafazenda.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/** Tokens opacos (refresh e convite): o texto vai para o cliente, o banco guarda só o SHA-256. */
public final class TokenSeguro {

    private static final int TAMANHO_EM_BYTES = 32;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private TokenSeguro() {
    }

    public static String gerar() {
        byte[] bytes = new byte[TAMANHO_EM_BYTES];
        ALEATORIO.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível na JVM", e);
        }
    }
}
