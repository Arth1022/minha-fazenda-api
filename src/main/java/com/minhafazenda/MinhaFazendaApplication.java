package com.minhafazenda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MinhaFazendaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinhaFazendaApplication.class, args);
    }

}
