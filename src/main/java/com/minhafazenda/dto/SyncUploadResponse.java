package com.minhafazenda.dto;

import java.util.List;

public record SyncUploadResponse(int aplicadas, List<OpRejeitada> rejeitadas) {
}
