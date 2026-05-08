package com.hector.chatbotwhatsapp.service;

import com.hector.chatbotwhatsapp.client.GroqClient;
import com.hector.chatbotwhatsapp.dto.GroqRequestDTO;
import com.hector.chatbotwhatsapp.dto.GroqRequestDTO.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final GroqClient groqClient;
    private final StringRedisTemplate redisTemplate;

    private static final String SYSTEM_PROMPT = """
            Você é um assistente configurável. Substitua este prompt pelo personagem desejado.
            Responda sempre em português do Brasil.
            Respostas curtas e diretas, máximo 3 linhas.
            """;

    private static final String MODEL = "llama3-8b-8192";
    private static final int MAX_TOKENS = 150;
    private static final int MAX_HISTORY = 10;
    private static final String REDIS_PREFIX = "chat:context:";
    private static final Duration CONTEXT_TTL = Duration.ofHours(2);

    public String generateResponse(String groupId, String userName, String texto) {
        String redisKey = REDIS_PREFIX + groupId;

        List<MessageDTO> history = loadHistory(redisKey);

        history.add(new MessageDTO("user", userName + ": " + texto));

        GroqRequestDTO request = new GroqRequestDTO(MODEL, MAX_TOKENS, SYSTEM_PROMPT, history);

        try {
            String resposta = groqClient.chat(request)
                    .choices()
                    .get(0)
                    .message()
                    .content();

            history.add(new MessageDTO("assistant", resposta));
            saveHistory(redisKey, history);

            return resposta;
        } catch (Exception e) {
            log.error("Erro ao chamar Groq API: {}", e.getMessage());
            return "...";
        }
    }

    private List<MessageDTO> loadHistory(String redisKey) {
        List<String> raw = redisTemplate.opsForList().range(redisKey, 0, -1);
        if (raw == null || raw.isEmpty()) return new ArrayList<>();

        List<MessageDTO> history = new ArrayList<>();
        for (int i = 0; i < raw.size() - 1; i += 2) {
            history.add(new MessageDTO(raw.get(i), raw.get(i + 1)));
        }

        if (history.size() > MAX_HISTORY) {
            history = history.subList(history.size() - MAX_HISTORY, history.size());
        }

        return history;
    }

    private void saveHistory(String redisKey, List<MessageDTO> history) {
        redisTemplate.delete(redisKey);
        for (MessageDTO msg : history) {
            redisTemplate.opsForList().rightPush(redisKey, msg.role());
            redisTemplate.opsForList().rightPush(redisKey, msg.content());
        }
        redisTemplate.expire(redisKey, CONTEXT_TTL);
    }
}