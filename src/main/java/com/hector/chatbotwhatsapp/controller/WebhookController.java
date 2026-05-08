package com.hector.chatbotwhatsapp.controller;

import com.hector.chatbotwhatsapp.dto.WebhookPayloadDTO;
import com.hector.chatbotwhatsapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<Void> receive(@RequestBody WebhookPayloadDTO payload) {
        log.info("Webhook recebido: event={}, instance={}", payload.event(), payload.instance());

        if ("messages.upsert".equals(payload.event())) {
            messageService.process(payload);
        }

        return ResponseEntity.ok().build();
    }
}
