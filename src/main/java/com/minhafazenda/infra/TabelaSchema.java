package com.minhafazenda.infra;

import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.jackson.databind.JsonNode;

/**
 * Colunas reais de uma tabela sincronizável. Todo nome usado no SQL do sync sai daqui,
 * nunca do que o app enviou.
 *
 * @param tipoPorColuna nome da coluna para o tipo do Postgres (udt_name)
 */
public record TabelaSchema(String nome, Map<String, String> tipoPorColuna) {

    public static final String FAZENDA_ID = "fazenda_id";
    public static final String DELETED_AT = "deleted_at";

    private static final Set<String> COLUNAS_SO_DO_SERVIDOR = Set.of("id", FAZENDA_ID, "created_by", "server_updated_at");

    /** Filtra o data do app: colunas desconhecidas e as controladas pelo servidor são ignoradas. */
    public List<ValorColuna> valoresGravaveis(Map<String, JsonNode> data) {
        return data.entrySet().stream()
                .filter(campo -> tipoPorColuna.containsKey(campo.getKey()))
                .filter(campo -> !COLUNAS_SO_DO_SERVIDOR.contains(campo.getKey()))
                .map(campo -> new ValorColuna(campo.getKey(), tipoPorColuna.get(campo.getKey()), comoTexto(campo.getValue())))
                .toList();
    }

    private static String comoTexto(JsonNode valor) {
        if (valor == null || valor.isNull()) {
            return null;
        }
        return valor.isValueNode() ? valor.asString() : valor.toString();
    }
}
