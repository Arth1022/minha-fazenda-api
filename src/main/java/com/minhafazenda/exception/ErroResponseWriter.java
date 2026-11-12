package com.minhafazenda.exception;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.json.JsonMapper;

/** Escreve erros no formato do contrato fora do alcance do {@link ApiExceptionHandler} (filtros). */
@Component
@RequiredArgsConstructor
public class ErroResponseWriter {

    private final JsonMapper jsonMapper;

    public void escrever(HttpServletResponse response, CodigoErro codigo) throws IOException {
        response.setStatus(codigo.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        jsonMapper.writeValue(response.getWriter(), ErroResponse.de(codigo));
    }
}
