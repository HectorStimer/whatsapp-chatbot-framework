package com.hector.chatbotwhatsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public record GroqRequestDTO(
        String model,
        @JsonProperty("max_tokens") int maxTokens,
        List<MessageDTO> messages
) {

    public GroqRequestDTO(String model, int maxTokens, String systemPrompt, List<MessageDTO> history) {
        this(model, maxTokens, buildMessages(systemPrompt, history));
    }

    private static List<MessageDTO> buildMessages(String systemPrompt, List<MessageDTO> history) {
        List<MessageDTO> messages = new ArrayList<>();
        messages.add(new MessageDTO("system", systemPrompt));
        messages.addAll(history);
        return messages;
    }

    public record MessageDTO(String role, String content) {}


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GroqResponseDTO(List<ChoiceDTO> choices) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record ChoiceDTO(MessageDTO message) {}
    }
}