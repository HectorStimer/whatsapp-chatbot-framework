package com.hector.chatbotwhatsapp.dto;

public record SendMessageDTO(
        String number,
        TextDTO text
) {
    public record TextDTO(String message) {}
}
