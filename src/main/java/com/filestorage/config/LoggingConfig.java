package com.filestorage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class LoggingConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Отключено, чтобы избежать конфликта с SecurityConfig
        // Публичные пути настроены в SecurityConfig через permitAll()
        return (web) -> web.debug(false);
    }
}