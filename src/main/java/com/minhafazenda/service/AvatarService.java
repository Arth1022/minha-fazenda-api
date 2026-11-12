package com.minhafazenda.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.infra.ArmazenamentoAvatar;
import com.minhafazenda.infra.ProcessadorImagem;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private static final long TAMANHO_MAXIMO_EM_BYTES = 5L * 1024 * 1024;

    private final ProcessadorImagem processadorImagem;
    private final ArmazenamentoAvatar armazenamento;
    private final PerfilService perfilService;

    /** O upload acontece fora da transação; só a troca do avatar_url é transacional. */
    public String trocar(UUID usuarioId, MultipartFile arquivo) {
        if (arquivo.getSize() > TAMANHO_MAXIMO_EM_BYTES) {
            throw new ApiException(CodigoErro.ARQUIVO_GRANDE);
        }
        byte[] jpeg = processadorImagem.paraJpegQuadrado(lerBytes(arquivo));
        String caminho = "%s/%s.jpg".formatted(usuarioId, UUID.randomUUID());
        String url = armazenamento.salvar(caminho, jpeg);
        perfilService.definirAvatar(usuarioId, url);
        return url;
    }

    public void remover(UUID usuarioId) {
        perfilService.removerAvatar(usuarioId);
    }

    private static byte[] lerBytes(MultipartFile arquivo) {
        try {
            return arquivo.getBytes();
        } catch (IOException e) {
            throw new ApiException(CodigoErro.VALIDACAO, "arquivo: não foi possível ler o envio");
        }
    }
}
