package com.hector.chatbotwhatsapp.service;

import com.hector.chatbotwhatsapp.dto.WebhookPayloadDTO;
import com.hector.chatbotwhatsapp.dto.WebhookPayloadDTO.MessageDTO;
import com.hector.chatbotwhatsapp.model.MessageLog;
import com.hector.chatbotwhatsapp.repository.MessageLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final AiService aiService;
    private final WhatsAppService whatsAppService;
    private final MessageLogRepository messageLogRepository;

    @Value("${bot.trigger-words}")
    private List<String> triggerWords;

    @Value("${bot.random-response-chance}")
    private int randomResponseChance;

    private final Random random = new Random();

    public void process(WebhookPayloadDTO payload) {
        if (payload.data() == null || payload.data().isEmpty()) return;

        for (MessageDTO msg : payload.data()) {
            if (isFromMe(msg)) continue;
            if (!isGroupMessage(msg)) continue;

            String texto = extractText(msg);
            if (texto == null || texto.isBlank()) continue;

            if (!shouldRespond(texto)) continue;

            String groupId = msg.key().remoteJid();
            String userName = msg.pushName() != null ? msg.pushName() : "ser humano";

            log.info("Respondendo para {} no grupo {}", userName, groupId);

            String resposta = aiService.generateResponse(groupId, userName, texto);
            whatsAppService.sendMessage(groupId, resposta);

            messageLogRepository.save(MessageLog.builder()
                    .groupId(groupId)
                    .userName(userName)
                    .message(texto)
                    .response(resposta)
                    .build());
        }
    }

    private boolean isFromMe(MessageDTO msg) {
        return Boolean.TRUE.equals(msg.key().fromMe());
    }

    private boolean isGroupMessage(MessageDTO msg) {
        return msg.key().remoteJid() != null && msg.key().remoteJid().endsWith("@g.us");
    }

    private String extractText(MessageDTO msg) {
        if (msg.message() == null) return null;
        return msg.message().getText();
    }

    private boolean shouldRespond(String texto) {
        String lower = texto.toLowerCase();

        boolean hasTrigger = triggerWords.stream().anyMatch(lower::contains);
        if (hasTrigger) return true;

        return random.nextInt(100) < randomResponseChance;
    }
}