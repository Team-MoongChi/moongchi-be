package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.dto.ChatMessageRequestDto;
import com.moongchi.moongchi_be.domain.chat.dto.MessageDto;
import com.moongchi.moongchi_be.domain.chat.entity.ChatMessage;
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

    /** 일반 메시지 저장 및 전송 */
    public MessageDto sendMessage(Long chatRoomId, Long participantId, ChatMessageRequestDto req) {
        // participant 조회 및 검증
        Participant participant = partRepo.findById(participantId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        ChatMessage saved = messageRepo.save(
                ChatMessage.builder()
                        .chatRoomId(chatRoomId)
                        .participantId(participantId)
                        .message(req.getMessage())
                        .messageType(MessageType.TEXT)
                        .sendAt(LocalDateTime.now())
                        .build()
        );

        MessageDto dto = MessageDto.from(saved);
        messagingTemplate.convertAndSend("/topic/chatroom." + chatRoomId, dto);
        return dto;
    }


    /** 시스템 메시지 전송용 */
    public void sendSystemMessage(Long chatRoomId, String message) {
        ChatMessage systemMsg = ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .participantId(null) // 시스템 메시지는 참여자 없음
                .message(message)
                .messageType(MessageType.SYSTEM)
                .sendAt(LocalDateTime.now())
                .build();

        messageRepo.save(systemMsg);

        MessageDto dto = MessageDto.from(systemMsg);
        messagingTemplate.convertAndSend("/topic/chatroom." + chatRoomId, dto);
    }

}
