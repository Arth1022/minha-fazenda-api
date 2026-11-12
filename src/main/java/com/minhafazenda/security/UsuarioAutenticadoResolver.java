package com.minhafazenda.security;

import java.util.UUID;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;

@Component
public class UsuarioAutenticadoResolver implements HandlerMethodArgumentResolver {

    public static final String CLAIM_SESSAO = "sid";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UsuarioAutenticado.class);
    }

    @Override
    public UsuarioAutenticado resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (!(autenticacao instanceof JwtAuthenticationToken token)) {
            throw new ApiException(CodigoErro.TOKEN_INVALIDO);
        }
        Jwt jwt = token.getToken();
        return new UsuarioAutenticado(
                UUID.fromString(jwt.getSubject()),
                UUID.fromString(jwt.getClaimAsString(CLAIM_SESSAO)));
    }
}
