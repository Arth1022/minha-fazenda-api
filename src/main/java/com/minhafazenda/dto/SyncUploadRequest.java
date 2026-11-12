package com.minhafazenda.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SyncUploadRequest(@NotNull Integer schemaVersion, @NotNull List<@NotNull @Valid SyncOp> ops) {
}
