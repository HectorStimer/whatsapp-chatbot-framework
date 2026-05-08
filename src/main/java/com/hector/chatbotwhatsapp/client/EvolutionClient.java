package com.hector.chatbotwhatsapp.client;

import com.hector.chatbotwhatsapp.dto.SendMessageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "evolution", url = "${evolution.base-url}")
public interface EvolutionClient {

    @PostMapping("/message/sendText/{instance}")
    void sendText(@PathVariable String instance, @RequestBody SendMessageDTO request);
}
