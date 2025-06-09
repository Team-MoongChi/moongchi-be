package com.moongchi.moongchi_be.domain.chat.repository;

import com.moongchi.moongchi_be.domain.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage,String> {
    List<ChatMessage> findByChatRoomIdOrderBySendAtAsc(String chatRoomId);
    Optional<ChatMessage> findFirstByChatRoomIdOrderBySendAtDesc(String chatRoomId);
}
