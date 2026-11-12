package com.minhafazenda.midia;

import java.util.Arrays;
import java.util.Optional;

/**
 * Tipos de arquivo que o app envia. Cada um vai para uma pasta própria dentro da pasta da
 * fazenda no bucket: {fazendaId}/{pasta}/{id}.{extensão}.
 */
public enum TipoMidia {

    IMAGEM("imagem", "imagens", 10L * 1024 * 1024),
    AUDIO("audio", "audios", 20L * 1024 * 1024),
    DOCUMENTO("documento", "documentos", 10L * 1024 * 1024);

    private final String valor;
    private final String pasta;
    private final long limiteEmBytes;

    TipoMidia(String valor, String pasta, long limiteEmBytes) {
        this.valor = valor;
        this.pasta = pasta;
        this.limiteEmBytes = limiteEmBytes;
    }

    public String valor() {
        return valor;
    }

    public String pasta() {
        return pasta;
    }

    public long limiteEmBytes() {
        return limiteEmBytes;
    }

    public long limiteEmMegabytes() {
        return limiteEmBytes / (1024 * 1024);
    }

    public static Optional<TipoMidia> doValor(String valor) {
        return Arrays.stream(values()).filter(tipo -> tipo.valor.equalsIgnoreCase(valor)).findFirst();
    }

    public static Optional<TipoMidia> daPasta(String pasta) {
        return Arrays.stream(values()).filter(tipo -> tipo.pasta.equals(pasta)).findFirst();
    }
}
