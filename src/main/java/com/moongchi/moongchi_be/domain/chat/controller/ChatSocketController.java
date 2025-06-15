package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.dto.ChatMessageRequestDto;
import com.moongchi.moongchi_be.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatSocketController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageRequestDto dto) {
        try {
            if ("SYSTEM".equalsIgnoreCase(dto.getMessageType())) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }

            chatMessageService.sendMessage(
                    dto.getChatRoomId(),
                    dto.getParticipantId(),
                    dto
            );

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }
}
