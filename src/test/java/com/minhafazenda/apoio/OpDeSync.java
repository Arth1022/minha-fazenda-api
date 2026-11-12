package com.minhafazenda.apoio;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Operações da fila do app, no formato de POST /sync/upload. */
public final class OpDeSync {

    /** Colunas obrigatórias de animais, para os testes mandarem só o que importa a eles. */
    private static final Map<String, Object> ANIMAL_MINIMO = Map.of(
            "brinco", "1023",
            "sexo", "F",
            "nascimento_aproximado", false,
            "ja_pariu", false,
            "crias_anteriores", 0,
            "status", "ativo",
            "parto_assistido", false);

    private OpDeSync() {
    }

    public static Map<String, Object> putAnimal(Object id, Map<String, Object> data) {
        Map<String, Object> completo = new HashMap<>(ANIMAL_MINIMO);
        completo.putAll(data);
        return put("animais", id, completo);
    }

    public static Map<String, Object> put(String tabela, Object id, Map<String, Object> data) {
        return op("PUT", tabela, id, data);
    }

    public static Map<String, Object> patch(String tabela, Object id, Map<String, Object> data) {
        return op("PATCH", tabela, id, data);
    }

    public static Map<String, Object> delete(String tabela, UUID id) {
        return Map.of("op", "DELETE", "table", tabela, "id", id.toString());
    }

    private static Map<String, Object> op(String tipo, String tabela, Object id, Map<String, Object> data) {
        return Map.of("op", tipo, "table", tabela, "id", id.toString(), "data", data);
    }
}
