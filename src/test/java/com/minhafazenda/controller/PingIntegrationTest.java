package com.minhafazenda.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import com.minhafazenda.apoio.IntegracaoTest;

class PingIntegrationTest extends IntegracaoTest {

    @Test
    void pingRespondeSemToken() throws Exception {
        api.getAnonimo("/ping").andExpect(status().isNoContent());
    }
}
