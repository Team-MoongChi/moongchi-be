package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.domain.chat.entity.ChatMessage;
import com.moongchi.moongchi_be.domain.chat.entity.MessageType;
import com.moongchi.moongchi_be.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private ChatMessageRepository chatMessageRepository;

    public void sendSystemMessage(String chatRoomId, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoomId(chatRoomId);
        chatMessage.setMessageType(MessageType.TEXT);
        chatMessage.setSendAt(LocalDateTime.now());
        chatMessage.setUserId("SYSTEM");
        chatMessage.setMessage(message);

        chatMessageRepository.save(chatMessage);
    }
}
