package com.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot application entry point for Security AI Platform.
 * Initializes the application with all necessary configurations and components.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@ComponentScan(basePackages = {"com.security"})
@EnableJpaRepositories(basePackages = {"com.security.repository"})
public class SecurityAIPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityAIPlatformApplication.class, args);
    }
}
