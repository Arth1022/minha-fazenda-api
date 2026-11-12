package com.minhafazenda.apoio;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Primary;

import com.minhafazenda.infra.ArmazenamentoAvatar;

/** Substitui o Supabase Storage nos testes, guardando os arquivos por caminho. */
@Primary
public class ArmazenamentoEmMemoria implements ArmazenamentoAvatar {

    private static final String URL_BASE = "https://storage.teste/avatars/";

    private final Map<String, byte[]> arquivos = new ConcurrentHashMap<>();

    @Override
    public String salvar(String caminho, byte[] jpeg) {
        arquivos.put(caminho, jpeg);
        return URL_BASE + caminho;
    }

    @Override
    public void apagar(String url) {
        arquivos.remove(url.substring(URL_BASE.length()));
    }

    public byte[] conteudo(String url) {
        return arquivos.get(url.substring(URL_BASE.length()));
    }

    public List<String> caminhosDe(UUID usuarioId) {
        return arquivos.keySet().stream().filter(caminho -> caminho.startsWith(usuarioId + "/")).toList();
    }
}
