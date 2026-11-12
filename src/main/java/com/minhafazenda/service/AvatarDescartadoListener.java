package com.minhafazenda.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.minhafazenda.infra.ArmazenamentoAvatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvatarDescartadoListener {

    private final ArmazenamentoAvatar armazenamento;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void apagarArquivo(AvatarDescartado evento) {
        try {
            armazenamento.apagar(evento.url());
        } catch (RuntimeException e) {
            // O perfil já foi salvo; um arquivo órfão não pode derrubar a resposta.
            log.warn("Não foi possível apagar o avatar antigo {}", evento.url(), e);
        }
    }
}
