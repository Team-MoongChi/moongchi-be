package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.dto.ChatMessageRequestDto;
import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import com.moongchi.moongchi_be.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatSocketController {

    private final ChatMessageService chatMessageService;
    private final ParticipantRepository participantRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequestDto dto, Principal principal) {
        try {
            if ("SYSTEM".equalsIgnoreCase(dto.getMessageType())) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
            Long userId = getUserIdFromPrincipal(principal);

            Participant participant = participantRepository
                    .findWithChatRoomByChatRoomIdAndUserId(dto.getChatRoomId(),userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN));

            chatMessageService.sendMessage(participant, dto);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return Long.parseLong(principal.getName());
    }
}
