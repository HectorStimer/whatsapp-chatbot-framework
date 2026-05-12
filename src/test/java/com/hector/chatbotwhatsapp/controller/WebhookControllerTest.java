package com.hector.chatbotwhatsapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hector.chatbotwhatsapp.dto.WebhookPayloadDTO;
import com.hector.chatbotwhatsapp.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @Test
    void deveRetornar200QuandoReceberWebhook() throws Exception {
        WebhookPayloadDTO payload = new WebhookPayloadDTO(
                "messages.upsert",
                "chatbot",
                List.of()
        );

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(messageService, times(1)).process(any());
    }

    @Test
    void deveIgnorarEventosDiferentesDeMessagesUpsert() throws Exception {
        WebhookPayloadDTO payload = new WebhookPayloadDTO(
                "connection.update",
                "chatbot",
                List.of()
        );

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(messageService, never()).process(any());
    }
}