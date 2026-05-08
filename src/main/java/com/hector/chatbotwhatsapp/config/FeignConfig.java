package com.hector.chatbotwhatsapp.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${groq.api-key}")
    private String groqApiKey;

    @Value("${evolution.api-key}")
    private String evolutionApiKey;

    @Bean
    public RequestInterceptor groqAuthInterceptor() {
        return template -> {
            if (template.feignTarget().name().equals("groq")) {
                template.header("Authorization", "Bearer " + groqApiKey);
            }
        };
    }

    @Bean
    public RequestInterceptor evolutionAuthInterceptor() {
        return template -> {
            if (template.feignTarget().name().equals("evolution")) {
                template.header("apikey", evolutionApiKey);
            }
        };
    }
}