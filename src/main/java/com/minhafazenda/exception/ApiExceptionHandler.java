package com.minhafazenda.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErroResponse> aoFalharRegraDaApi(ApiException excecao) {
        return ResponseEntity.status(excecao.getCodigo().getStatus()).body(ErroResponse.de(excecao));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErroResponse> aoFalharSemPrevisao(Exception excecao) {
        log.error("Erro inesperado", excecao);
        return ResponseEntity.internalServerError().body(ErroResponse.de(CodigoErro.ERRO_INTERNO));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException excecao,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String mensagem = excecao.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(ApiExceptionHandler::descrever)
                .orElse(CodigoErro.VALIDACAO.getMensagemPadrao());
        return ResponseEntity.badRequest().body(new ErroResponse(CodigoErro.VALIDACAO.name(), mensagem));
    }

    /** Converte os erros padrão do Spring MVC para o formato do contrato. */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception excecao, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        CodigoErro codigo = codigoPara(status);
        if (codigo == CodigoErro.ERRO_INTERNO) {
            log.error("Erro inesperado", excecao);
        }
        return ResponseEntity.status(status).headers(headers).body(ErroResponse.de(codigo));
    }

    private static String descrever(FieldError erro) {
        return "%s: %s".formatted(erro.getField(), erro.getDefaultMessage());
    }

    private static CodigoErro codigoPara(HttpStatusCode status) {
        if (status.is5xxServerError()) {
            return CodigoErro.ERRO_INTERNO;
        }
        return switch (status.value()) {
            case 404 -> CodigoErro.NAO_ENCONTRADO;
            case 413 -> CodigoErro.ARQUIVO_GRANDE;
            default -> CodigoErro.VALIDACAO;
        };
    }
}
