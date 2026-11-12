package com.minhafazenda.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.minhafazenda.repository.UsuarioRepository;

class DevCredencialVerifierPerfilTest {

    private final ApplicationContextRunner contexto = new ApplicationContextRunner()
            .withBean(UsuarioRepository.class, () -> mock(UsuarioRepository.class))
            .withUserConfiguration(DevCredencialVerifier.class, CredencialVerifiers.class);

    @Test
    void existeNoPerfilDev() {
        contexto.withPropertyValues("spring.profiles.active=dev")
                .run(aplicacao -> assertThat(aplicacao).hasSingleBean(DevCredencialVerifier.class));
    }

    @Test
    void naoExisteNoPerfilProd() {
        contexto.withPropertyValues("spring.profiles.active=prod").run(aplicacao -> {
            assertThat(aplicacao).doesNotHaveBean(DevCredencialVerifier.class);
            assertThat(aplicacao).doesNotHaveBean(CredencialVerifier.class);
        });
    }
}
