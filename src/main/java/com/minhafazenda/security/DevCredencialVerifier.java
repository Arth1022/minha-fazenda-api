package com.minhafazenda.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.minhafazenda.dto.CredencialRequest;
import com.minhafazenda.entity.Usuario;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.repository.UsuarioRepository;
import com.minhafazenda.service.EmailUsuario;

import lombok.RequiredArgsConstructor;

/** Aceita qualquer e-mail sem verificação. Existe só no perfil dev. */
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevCredencialVerifier implements CredencialVerifier {

    private static final String TIPO = "dev";

    private final UsuarioRepository usuarios;

    @Override
    public boolean suporta(String tipo) {
        return TIPO.equals(tipo);
    }

    @Override
    public Usuario registrar(String nome, CredencialRequest credencial) {
        String email = EmailUsuario.normalizar(credencial.email());
        if (usuarios.existsByEmail(email)) {
            throw new ApiException(CodigoErro.VALIDACAO, "credencial.email: já existe uma conta com este e-mail");
        }
        return usuarios.save(new Usuario(nome, email, null, null));
    }

    @Override
    public Usuario autenticar(CredencialRequest credencial) {
        return usuarios.findByEmail(EmailUsuario.normalizar(credencial.email()))
                .orElseThrow(() -> new ApiException(CodigoErro.NAO_ENCONTRADO, "Conta não encontrada."));
    }
}
