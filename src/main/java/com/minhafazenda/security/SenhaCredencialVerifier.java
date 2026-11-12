package com.minhafazenda.security;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.minhafazenda.dto.CredencialRequest;
import com.minhafazenda.entity.Usuario;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.repository.UsuarioRepository;
import com.minhafazenda.service.EmailUsuario;

/** Login por e-mail e senha. O banco guarda só o hash (bcrypt) em usuarios.credencial_hash. */
// TODO(decisao): login por código (o "code" da arquitetura); por enquanto o método real é e-mail e senha.
@Component
public class SenhaCredencialVerifier implements CredencialVerifier {

    private static final String TIPO = "senha";
    private static final int MINIMO_CARACTERES = 8;
    // O bcrypt ignora o que passa de 72 bytes
    private static final int MAXIMO_BYTES = 72;

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    // Comparado quando o e-mail não existe, para a resposta levar o mesmo tempo
    private final String hashSemDono;

    public SenhaCredencialVerifier(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.hashSemDono = encoder.encode(UUID.randomUUID().toString());
    }

    @Override
    public boolean suporta(String tipo) {
        return TIPO.equals(tipo);
    }

    @Override
    public Usuario registrar(String nome, CredencialRequest credencial) {
        String email = EmailUsuario.normalizar(credencial.email());
        String senha = senhaValida(credencial.senha());
        if (usuarios.existsByEmail(email)) {
            throw new ApiException(CodigoErro.VALIDACAO, "credencial.email: já existe uma conta com este e-mail");
        }
        return usuarios.save(new Usuario(nome, email, null, encoder.encode(senha)));
    }

    @Override
    public Usuario autenticar(CredencialRequest credencial) {
        Optional<Usuario> usuario = usuarios.findByEmail(EmailUsuario.normalizar(credencial.email()));
        String hash = usuario.map(Usuario::getCredencialHash).filter(StringUtils::hasText).orElse(hashSemDono);
        String senha = credencial.senha() == null ? "" : credencial.senha();
        if (!encoder.matches(senha, hash) || usuario.isEmpty()) {
            throw new ApiException(CodigoErro.CREDENCIAL_INVALIDA);
        }
        return usuario.get();
    }

    private static String senhaValida(String senha) {
        if (senha == null || senha.length() < MINIMO_CARACTERES
                || senha.getBytes(StandardCharsets.UTF_8).length > MAXIMO_BYTES) {
            throw new ApiException(CodigoErro.VALIDACAO,
                    "credencial.senha: deve ter no mínimo %d caracteres e no máximo %d bytes"
                            .formatted(MINIMO_CARACTERES, MAXIMO_BYTES));
        }
        return senha;
    }
}
