package com.hector.chatbotwhatsapp.client;

import com.hector.chatbotwhatsapp.dto.GroqRequestDTO;
import com.hector.chatbotwhatsapp.dto.GroqRequestDTO.GroqResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "groq", url = "${groq.base-url}")
public interface GroqClient {

    @PostMapping("/openai/v1/chat/completions")
    GroqResponseDTO chat(@RequestBody GroqRequestDTO request);
}