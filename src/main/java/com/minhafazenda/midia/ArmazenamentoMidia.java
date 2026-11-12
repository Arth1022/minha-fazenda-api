package com.minhafazenda.midia;

import java.time.Duration;

/** Onde as mídias das fazendas ficam guardadas (Supabase Storage pelo S3; memória nos testes). */
public interface ArmazenamentoMidia {

    void salvar(String caminho, byte[] conteudo, String contentType);

    /** URL de leitura temporária: o bucket é privado, nada fica com link público. */
    String urlAssinada(String caminho, Duration validade);

    boolean existe(String caminho);

    void apagar(String caminho);
}
