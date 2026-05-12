package com.hector.chatbotwhatsapp.repository;

import com.hector.chatbotwhatsapp.model.MessageLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageLogRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MessageLogRepository repository;

    @Test
    void deveSalvarEBuscarMensagensPorGrupo() {
        repository.save(MessageLog.builder()
                .groupId("123456789@g.us")
                .userName("Hector")
                .message("tung tung!")
                .response("TUNG TUNG TUNG")
                .build());

        repository.save(MessageLog.builder()
                .groupId("123456789@g.us")
                .userName("Amigo")
                .message("cajado!")
                .response("...")
                .build());

        List<MessageLog> logs = repository.findTop10ByGroupIdOrderByCreatedAtDesc("123456789@g.us");

        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).getUserName()).isEqualTo("Amigo");
    }

    @Test
    void deveRetornarVazioParaGrupoInexistente() {
        List<MessageLog> logs = repository.findTop10ByGroupIdOrderByCreatedAtDesc("grupo-inexistente@g.us");
        assertThat(logs).isEmpty();
    }
}