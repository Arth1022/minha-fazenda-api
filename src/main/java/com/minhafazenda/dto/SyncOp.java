package com.minhafazenda.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.JsonNode;

public record SyncOp(
        @NotNull TipoOp op,
        @NotBlank @Size(max = 63) String table,
        @NotBlank @Size(max = 36) String id,
        Map<String, JsonNode> data) {

    public SyncOp {
        data = data == null ? Map.of() : data;
    }
}
