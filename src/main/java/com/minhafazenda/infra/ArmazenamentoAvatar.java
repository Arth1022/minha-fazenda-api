package com.minhafazenda.infra;

public interface ArmazenamentoAvatar {

    /** Salva o JPEG no caminho informado e devolve a URL pública direta. */
    String salvar(String caminho, byte[] jpeg);

    void apagar(String url);
}
