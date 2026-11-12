package com.minhafazenda.midia;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * Formatos aceitos, reconhecidos pela assinatura nos primeiros bytes (nunca pelo Content-Type
 * ou pela extensão que o app mandou).
 */
public enum FormatoMidia {

    JPEG(TipoMidia.IMAGEM, "jpg", "image/jpeg") {
        @Override
        boolean reconhece(byte[] b) {
            return comecaCom(b, 0, (byte) 0xFF, (byte) 0xD8, (byte) 0xFF);
        }
    },
    PNG(TipoMidia.IMAGEM, "png", "image/png") {
        @Override
        boolean reconhece(byte[] b) {
            return comecaCom(b, 0, (byte) 0x89, (byte) 'P', (byte) 'N', (byte) 'G');
        }
    },
    WEBP(TipoMidia.IMAGEM, "webp", "image/webp") {
        @Override
        boolean reconhece(byte[] b) {
            return comecaCom(b, 0, ascii("RIFF")) && comecaCom(b, 8, ascii("WEBP"));
        }
    },
    /** Gravação do expo-audio (AAC em MP4) e vídeos curtos do iPhone gravados como áudio. */
    M4A(TipoMidia.AUDIO, "m4a", "audio/mp4") {
        @Override
        boolean reconhece(byte[] b) {
            return comecaCom(b, 4, ascii("ftyp")) && b.length >= 12
                    && MARCAS_MP4_AUDIO.contains(new String(b, 8, 4, StandardCharsets.US_ASCII));
        }
    },
    MP3(TipoMidia.AUDIO, "mp3", "audio/mpeg") {
        @Override
        boolean reconhece(byte[] b) {
            return comecaCom(b, 0, ascii("ID3"))
                    || (b.length >= 2 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xE6) == 0xE2);
        }
    },
    AAC(TipoMidia.AUDIO, "aac", "audio/aac") {
        @Override
        boolean reconhece(byte[] b) {
            return b.length >= 2 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xF6) == 0xF0;
        }
    },
    OGG(TipoMidia.AUDIO, "ogg", "audio/ogg") {
        @Override
        boolean reconhece(byte[] b) {
            return comecaCom(b, 0, ascii("OggS"));
        }
    },
    WEBM(TipoMidia.AUDIO, "webm", "audio/webm") {
        @Override
        boolean reconhece(byte[] b) {
            return comecaCom(b, 0, (byte) 0x1A, (byte) 0x45, (byte) 0xDF, (byte) 0xA3);
        }
    },
    WAV(TipoMidia.AUDIO, "wav", "audio/wav") {
        @Override
        boolean reconhece(byte[] b) {
            return comecaCom(b, 0, ascii("RIFF")) && comecaCom(b, 8, ascii("WAVE"));
        }
    },
    PDF(TipoMidia.DOCUMENTO, "pdf", "application/pdf") {
        @Override
        boolean reconhece(byte[] b) {
            return comecaCom(b, 0, ascii("%PDF"));
        }
    };

    private static final Set<String> MARCAS_MP4_AUDIO = Set.of("M4A ", "M4B ", "mp41", "mp42", "isom", "iso2",
            "3gp4", "3gp5", "3gp6", "3g2a", "dash", "MSNV");

    private final TipoMidia tipo;
    private final String extensao;
    private final String contentType;

    FormatoMidia(TipoMidia tipo, String extensao, String contentType) {
        this.tipo = tipo;
        this.extensao = extensao;
        this.contentType = contentType;
    }

    abstract boolean reconhece(byte[] bytes);

    public TipoMidia tipo() {
        return tipo;
    }

    public String extensao() {
        return extensao;
    }

    public String contentType() {
        return contentType;
    }

    /** O formato do arquivo, só entre os formatos do tipo pedido (uma foto não entra como áudio). */
    public static Optional<FormatoMidia> detectar(TipoMidia tipo, byte[] bytes) {
        return Arrays.stream(values())
                .filter(formato -> formato.tipo == tipo)
                .filter(formato -> formato.reconhece(bytes))
                .findFirst();
    }

    public static Optional<FormatoMidia> daExtensao(String extensao) {
        return Arrays.stream(values()).filter(formato -> formato.extensao.equals(extensao)).findFirst();
    }

    private static byte[] ascii(String texto) {
        return texto.getBytes(StandardCharsets.US_ASCII);
    }

    private static boolean comecaCom(byte[] bytes, int posicao, byte... assinatura) {
        int fim = posicao + assinatura.length;
        return bytes.length >= fim && Arrays.equals(bytes, posicao, fim, assinatura, 0, assinatura.length);
    }
}
