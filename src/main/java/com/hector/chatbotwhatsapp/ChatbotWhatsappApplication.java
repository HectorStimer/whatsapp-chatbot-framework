package com.hector.chatbotwhatsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ChatbotWhatsappApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotWhatsappApplication.class, args);
    }
}