package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.dto.ChatMessageRequestDto;
import com.moongchi.moongchi_be.domain.chat.dto.MessageDto;
import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import com.moongchi.moongchi_be.domain.chat.service.ChatMessageService;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "채팅", description = "채팅 관련 API")
@RestController
@RequestMapping("api/chat/rooms/{chatRoomId}/message")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final ParticipantRepository participantRepository;


    @Operation(summary = "채팅 메시지 전송", description = "채팅방에 메시지를 보냅니다.")
    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable Long chatRoomId,
            @RequestBody @Valid ChatMessageRequestDto request,
            HttpServletRequest servletRequest
    ) {
        User user = userService.getUser(servletRequest);

        Participant participant = participantRepository
                .findByChatRoomIdAndUserId(chatRoomId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN));
        MessageDto dto = chatMessageService.sendMessage(participant, request);
        return ResponseEntity.status(201).body(dto);
    }

}
