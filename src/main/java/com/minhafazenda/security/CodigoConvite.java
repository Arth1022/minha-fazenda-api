package com.minhafazenda.security;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Código de convite que a pessoa digita: 6 caracteres sem os que se confundem (0/O, 1/I/L).
 * 31^6 ≈ 887 milhões de combinações; com o limite de 10 tentativas por minuto em
 * /convites/aceitar e /auth/registro e validade de 24 h, adivinhar não é viável.
 */
public final class CodigoConvite {

    public static final int TAMANHO = 6;
    private static final String ALFABETO = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    private static final Pattern FORMATO = Pattern.compile("^[" + ALFABETO + "]{" + TAMANHO + "}$");
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private CodigoConvite() {
    }

    public static String gerar() {
        StringBuilder codigo = new StringBuilder(TAMANHO);
        for (int i = 0; i < TAMANHO; i++) {
            codigo.append(ALFABETO.charAt(ALEATORIO.nextInt(ALFABETO.length())));
        }
        return codigo.toString();
    }

    /** "abc-23 4" → "ABC234"; vazio se não tiver cara de código (aí é um token de link antigo). */
    public static Optional<String> normalizar(String digitado) {
        if (digitado == null) {
            return Optional.empty();
        }
        String limpo = digitado.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
        return FORMATO.matcher(limpo).matches() ? Optional.of(limpo) : Optional.empty();
    }
}
