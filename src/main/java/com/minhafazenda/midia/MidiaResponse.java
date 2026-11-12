package com.minhafazenda.midia;

import java.time.Instant;

/**
 * @param caminho  o que o app grava na coluna do registro (com o prefixo {@code midia:})
 * @param url      leitura temporária do arquivo
 * @param expiraEm quando a URL deixa de valer
 */
public record MidiaResponse(String caminho, String url, Instant expiraEm) {
}
