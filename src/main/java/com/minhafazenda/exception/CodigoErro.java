package com.minhafazenda.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CodigoErro {

    VALIDACAO(HttpStatus.BAD_REQUEST, "Dados inválidos."),
    TOKEN_INVALIDO(HttpStatus.UNAUTHORIZED, "Token de acesso ausente, vencido ou inválido."),
    SESSAO_ENCERRADA(HttpStatus.UNAUTHORIZED, "Sua sessão foi encerrada. Entre novamente."),
    CREDENCIAL_INVALIDA(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos."),
    SEM_PERMISSAO(HttpStatus.FORBIDDEN, "Você não tem permissão para fazer isso."),
    NAO_ENCONTRADO(HttpStatus.NOT_FOUND, "Não encontrado."),
    ULTIMO_OWNER(HttpStatus.CONFLICT, "A fazenda precisa de pelo menos um proprietário. Promova outra pessoa antes."),
    CONVITE_INVALIDO(HttpStatus.GONE, "Este convite venceu, foi revogado ou já foi usado."),
    ARQUIVO_GRANDE(HttpStatus.CONTENT_TOO_LARGE, "A foto precisa ter no máximo 5 MB."),
    FORMATO_INVALIDO(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "A foto precisa ser JPEG, PNG ou WebP."),
    APP_DESATUALIZADO(HttpStatus.UPGRADE_REQUIRED, "Atualize o aplicativo para continuar sincronizando."),
    MUITAS_TENTATIVAS(HttpStatus.TOO_MANY_REQUESTS, "Muitas tentativas. Aguarde um minuto e tente de novo."),
    ERRO_INTERNO(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado. Tente novamente.");

    private final HttpStatus status;
    private final String mensagemPadrao;
}
