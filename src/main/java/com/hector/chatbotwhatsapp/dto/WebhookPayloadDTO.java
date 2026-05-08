package com.hector.chatbotwhatsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookPayloadDTO(
        String event,
        String instance,
        List<MessageDTO> data
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MessageDTO(
            KeyDTO key,
            MessageContentDTO message,
            String pushName,
            Long messageTimestamp
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KeyDTO(
            String remoteJid,
            Boolean fromMe,
            String participant
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MessageContentDTO(
            String conversation,
            ExtendedTextDTO extendedTextMessage
    ) {
        public String getText() {
            if (conversation != null) return conversation;
            if (extendedTextMessage != null) return extendedTextMessage.text();
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ExtendedTextDTO(String text) {}
}