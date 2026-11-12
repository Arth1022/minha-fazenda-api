package com.minhafazenda.dto;

import java.time.Instant;

/**
 * @param codigo   o que o colaborador digita no app (6 caracteres)
 * @param mensagem texto pronto para o botão de compartilhar
 * @param expiraEm fim da validade (24 h depois de gerado)
 */
public record CodigoConviteResponse(String codigo, String mensagem, Instant expiraEm) {
}
