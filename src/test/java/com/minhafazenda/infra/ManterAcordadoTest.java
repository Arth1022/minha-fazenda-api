package com.minhafazenda.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

class ManterAcordadoTest {

    private final List<String> caminhosRecebidos = new CopyOnWriteArrayList<>();
    private HttpServer servidor;

    @BeforeEach
    void subirServidor() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/", troca -> {
            caminhosRecebidos.add(troca.getRequestURI().getPath());
            troca.sendResponseHeaders(204, -1);
            troca.close();
        });
        servidor.start();
    }

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
    }

    @Test
    void pingaOEndpointPingDaUrlPublica() {
        new ManterAcordado("http://127.0.0.1:" + servidor.getAddress().getPort()).pingar();

        assertThat(caminhosRecebidos).containsExactly("/ping");
    }

    @Test
    void falhaDeRedeSoViraLogENaoDerrubaOAgendamento() {
        int porta = servidor.getAddress().getPort();
        servidor.stop(0);

        assertThatCode(() -> new ManterAcordado("http://127.0.0.1:" + porta).pingar()).doesNotThrowAnyException();
    }
}
