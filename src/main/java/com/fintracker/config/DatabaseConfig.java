package com.fintracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.fintracker.core.repository")
@EnableTransactionManagement
@EnableJpaAuditing
public class DatabaseConfig {
    // Database configuration will be handled by Spring Boot's auto-configuration
    // based on application.yml properties
}