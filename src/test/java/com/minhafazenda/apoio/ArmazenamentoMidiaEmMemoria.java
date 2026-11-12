package com.minhafazenda.apoio;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Primary;

import com.minhafazenda.midia.ArmazenamentoMidia;

/** Substitui o bucket de mídias nos testes, guardando os arquivos por caminho. */
@Primary
public class ArmazenamentoMidiaEmMemoria implements ArmazenamentoMidia {

    public record Arquivo(byte[] conteudo, String contentType) {
    }

    private final Map<String, Arquivo> arquivos = new ConcurrentHashMap<>();

    @Override
    public void salvar(String caminho, byte[] conteudo, String contentType) {
        arquivos.put(caminho, new Arquivo(conteudo, contentType));
    }

    @Override
    public String urlAssinada(String caminho, Duration validade) {
        return "https://storage.teste/midias/" + caminho + "?X-Amz-Expires=" + validade.toSeconds();
    }

    @Override
    public boolean existe(String caminho) {
        return arquivos.containsKey(caminho);
    }

    @Override
    public void apagar(String caminho) {
        arquivos.remove(caminho);
    }

    public Arquivo arquivo(String caminho) {
        return arquivos.get(caminho);
    }
}
