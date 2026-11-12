package com.minhafazenda.enums;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrigemTarefa implements ValorNoBanco {
    OCORRENCIA("ocorrencia"),
    MANUAL("manual"),
    ETAPA_CICLO("etapa_ciclo"),
    TRATAMENTO("tratamento"),
    SUGESTAO_TRAJETO("sugestao_trajeto");

    private final String valor;

    @Converter(autoApply = true)
    public static class Conversor extends ConversorDeValor<OrigemTarefa> {
        public Conversor() {
            super(OrigemTarefa.class);
        }
    }
}
