package com.minhafazenda.infra;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

/** Formatos aceitos, reconhecidos pela assinatura nos primeiros bytes e não pelo Content-Type. */
public enum FormatoImagem {

    JPEG {
        @Override
        boolean reconhece(byte[] bytes) {
            return comecaCom(bytes, 0, new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
        }
    },
    PNG {
        @Override
        boolean reconhece(byte[] bytes) {
            return comecaCom(bytes, 0, new byte[] {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A});
        }
    },
    WEBP {
        @Override
        boolean reconhece(byte[] bytes) {
            return comecaCom(bytes, 0, "RIFF".getBytes(StandardCharsets.US_ASCII))
                    && comecaCom(bytes, 8, "WEBP".getBytes(StandardCharsets.US_ASCII));
        }
    };

    abstract boolean reconhece(byte[] bytes);

    public static Optional<FormatoImagem> detectar(byte[] bytes) {
        return Arrays.stream(values()).filter(formato -> formato.reconhece(bytes)).findFirst();
    }

    private static boolean comecaCom(byte[] bytes, int posicao, byte[] assinatura) {
        int fim = posicao + assinatura.length;
        return bytes.length >= fim && Arrays.equals(bytes, posicao, fim, assinatura, 0, assinatura.length);
    }
}
