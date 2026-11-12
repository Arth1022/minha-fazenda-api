package com.minhafazenda.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.minhafazenda.dto.LoginRequest;
import com.minhafazenda.dto.LoginResponse;
import com.minhafazenda.dto.RegistroRequest;
import com.minhafazenda.dto.RegistroResponse;
import com.minhafazenda.dto.SessaoAberta;
import com.minhafazenda.entity.Usuario;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.security.CredencialVerifiers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final CredencialVerifiers verifiers;
    private final SessaoService sessaoService;
    private final FazendaService fazendaService;
    private final ConviteService conviteService;

    /** Usuário, sessão e fazenda (nova ou a do convite) nascem na mesma transação. */
    @Transactional
    public RegistroResponse registrar(RegistroRequest request) {
        boolean criaFazenda = StringUtils.hasText(request.nomeFazenda());
        boolean usaConvite = StringUtils.hasText(request.conviteToken());
        if (criaFazenda == usaConvite) {
            throw new ApiException(CodigoErro.VALIDACAO, "Informe nomeFazenda ou conviteToken, e apenas um deles.");
        }
        Usuario usuario = verifiers.para(request.credencial().tipo()).registrar(request.nome(), request.credencial());
        UUID fazendaId = criaFazenda
                ? fazendaService.criar(usuario.getId(), null, request.nomeFazenda()).getId()
                : conviteService.aceitar(usuario.getId(), request.conviteToken());
        SessaoAberta sessao = sessaoService.abrir(usuario, request.deviceId());
        return RegistroResponse.de(sessao, usuario, fazendaId);
    }

    @Transactional
    public LoginResponse entrar(LoginRequest request) {
        Usuario usuario = verifiers.para(request.credencial().tipo()).autenticar(request.credencial());
        return LoginResponse.de(sessaoService.abrir(usuario, request.deviceId()), usuario);
    }
}
