package com.minhafazenda.apoio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Base dos testes de integração: API completa sobre um Postgres real (Testcontainers), com o
 * schema aplicado pelo Liquibase. O container é único para toda a suíte; cada teste cria as
 * próprias contas e fazendas, então não há limpeza entre eles.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import({ApiDeTeste.class, ArmazenamentoEmMemoria.class, ValidadorDeNomeProibido.class})
public abstract class IntegracaoTest {

    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:15-alpine");

    static {
        POSTGRES.start();
    }

    @Autowired
    protected ApiDeTeste api;

    @Autowired
    protected JdbcTemplate jdbc;

    @DynamicPropertySource
    static void conectarAoPostgres(DynamicPropertyRegistry propriedades) {
        propriedades.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        propriedades.add("spring.datasource.username", POSTGRES::getUsername);
        propriedades.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
