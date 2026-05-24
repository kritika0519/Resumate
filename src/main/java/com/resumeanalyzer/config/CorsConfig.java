package com.resumeanalyzer.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${frontend.url:}")
    private String frontendUrl;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                List<String> origins = new ArrayList<>(List.of(
                        "http://localhost:8080",
                        "http://localhost:3000",
                        "http://127.0.0.1:5500",
                        "https://*.vercel.app"
                ));

                if (frontendUrl != null && !frontendUrl.isBlank()) {
                    origins.add(frontendUrl);
                }

                registry.addMapping("/api/**")
                        .allowedOriginPatterns(origins.toArray(String[]::new))
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
}
