package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.domain.chat.dto.ChatMessageRequestDto;
import com.moongchi.moongchi_be.domain.chat.dto.MessageDto;
import com.moongchi.moongchi_be.domain.chat.entity.ChatMessage;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoomStatus;
import com.moongchi.moongchi_be.domain.chat.entity.MessageType;
import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import com.moongchi.moongchi_be.domain.chat.repository.ChatMessageRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ChatRoomRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository roomRepo;
    private final ParticipantRepository partRepo;
    private final ChatMessageRepository messageRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageDto sendMessage(Participant participant, ChatMessageRequestDto req) {
        Long chatRoomId = participant.getGroupBoard().getChatRoom().getId();

        ChatMessage saved = messageRepo.save(
                ChatMessage.builder()
                        .chatRoomId(chatRoomId)
                        .participantId(participant.getId())
                        .message(req.getMessage())
                        .messageType(MessageType.TEXT)
                        .sendAt(LocalDateTime.now())
                        .build()
        );

        MessageDto dto = MessageDto.from(saved);
        messagingTemplate.convertAndSend("/topic/chatroom." + chatRoomId, dto);
        return dto;
    }

    public void sendSystemMessage(Long chatRoomId, String message,ChatRoomStatus status,
                                  String chatStatus, String buttonVisibleTo) {
        ChatMessage systemMsg = ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .participantId(null)
                .message(message)
                .messageType(MessageType.SYSTEM)
                .sendAt(LocalDateTime.now())
                .build();

        ChatMessage saved = messageRepo.save(systemMsg);

        MessageDto dto = MessageDto.from(saved,status,chatStatus,buttonVisibleTo);
        messagingTemplate.convertAndSend("/topic/chatroom." + chatRoomId, dto);
    }

}
