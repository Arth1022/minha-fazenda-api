package com.minhafazenda.security;

import java.util.UUID;

/** Identidade extraída do access token. Fazenda e papel nunca vêm daqui: são lidos de fazenda_membros. */
public record UsuarioAutenticado(UUID id, UUID sessaoId) {
}
