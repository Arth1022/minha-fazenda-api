package com.minhafazenda.security;

import com.minhafazenda.dto.CredencialRequest;
import com.minhafazenda.entity.Usuario;

/** Um método de login: confere a credencial enviada pelo app e devolve o usuário dono dela. */
public interface CredencialVerifier {

    boolean suporta(String tipo);

    /** Cria a identidade no registro. */
    Usuario registrar(String nome, CredencialRequest credencial);

    Usuario autenticar(CredencialRequest credencial);
}
