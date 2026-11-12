package com.minhafazenda.infra;

/** Valor enviado pelo app para uma coluna conhecida, já em texto e com o tipo real do Postgres. */
public record ValorColuna(String coluna, String tipo, String valor) {
}
