package com.hector.chatbotwhatsapp.service;

import com.hector.chatbotwhatsapp.client.EvolutionClient;
import com.hector.chatbotwhatsapp.dto.SendMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final EvolutionClient evolutionClient;

    @Value("${evolution.instance}")
    private String instance;

    public void sendMessage(String groupId, String text) {
        try {
            SendMessageDTO request = new SendMessageDTO(groupId, new SendMessageDTO.TextDTO(text));
            evolutionClient.sendText(instance, request);
            log.info("Mensagem enviada para {}", groupId);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem: {}", e.getMessage());
        }
    }
}