package com.petry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AuditConfig {

    public static void main(String[] args) {
        SpringApplication.run(AuditConfig.class, args);
    }
}
