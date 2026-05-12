package com.hector.chatbotwhatsapp.repository;

import com.hector.chatbotwhatsapp.model.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {

    List<MessageLog> findTop10ByGroupIdOrderByCreatedAtDesc(String groupId);
}
