package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.domain.chat.dto.ChatMessageRequestDto;
import com.moongchi.moongchi_be.domain.chat.dto.MessageDto;
import com.moongchi.moongchi_be.domain.chat.entity.ChatMessage;
import com.moongchi.moongchi_be.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatSocketController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageRequestDto dto) {
        try {
        ChatMessage saved = chatMessageService.save(
                dto.getChatRoomId(),
                dto.getParticipantId(),
                dto.getMessage(),
                dto.getMessageType()
        );
        MessageDto sendDto = chatMessageService.toDto(saved);
        messagingTemplate.convertAndSend(
                "/topic/chatroom." + dto.getChatRoomId(),
                sendDto
        );
        }
        catch (Exception e) {
            log.error("채팅 메시지 처리 중 에러 발생!", e);
            throw e;
        }
    }
}
