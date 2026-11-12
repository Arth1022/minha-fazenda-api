package com.minhafazenda.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import lombok.extern.slf4j.Slf4j;

/**
 * No plano gratuito o Render desliga a API depois de 15 minutos sem requisições. Este ping pela
 * URL pública entra pelo balanceador como uma requisição de fora e mantém a instância ligada.
 */
@Slf4j
@Component
@ConditionalOnExpression("'${manter-acordado.url:}' != ''")
public class ManterAcordado {

    private final RestClient cliente;

    public ManterAcordado(@Value("${manter-acordado.url}") String urlPublica) {
        this.cliente = RestClient.create(urlPublica);
    }

    @Scheduled(fixedDelayString = "${manter-acordado.intervalo:PT10M}",
            initialDelayString = "${manter-acordado.intervalo:PT10M}")
    void pingar() {
        try {
            cliente.get().uri("/ping").retrieve().toBodilessEntity();
        } catch (RestClientException e) {
            log.warn("Ping para manter a API acordada falhou: {}", e.getMessage());
        }
    }
}
