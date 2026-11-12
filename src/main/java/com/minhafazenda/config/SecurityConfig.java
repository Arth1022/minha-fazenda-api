package com.minhafazenda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.exception.ErroResponseWriter;

@Configuration
public class SecurityConfig {

    private static final String[] ROTAS_PUBLICAS_DE_CONTA = {"/auth/registro", "/auth/login", "/auth/refresh"};

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ErroResponseWriter erroResponseWriter) throws Exception {
        AuthenticationEntryPoint semToken = (request, response, e) ->
                erroResponseWriter.escrever(response, CodigoErro.TOKEN_INVALIDO);
        AccessDeniedHandler semPermissao = (request, response, e) ->
                erroResponseWriter.escrever(response, CodigoErro.SEM_PERMISSAO);

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(rotas -> rotas
                        .requestMatchers(HttpMethod.POST, ROTAS_PUBLICAS_DE_CONTA).permitAll()
                        .requestMatchers(HttpMethod.GET, "/.well-known/**", "/ping").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(semToken)
                        .accessDeniedHandler(semPermissao))
                .exceptionHandling(erros -> erros
                        .authenticationEntryPoint(semToken)
                        .accessDeniedHandler(semPermissao))
                .build();
    }

    /** Bcrypt, com o prefixo {bcrypt} no hash para dar para trocar o algoritmo depois. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
