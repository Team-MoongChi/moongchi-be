package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.domain.chat.dto.ChatMessageRequestDto;
import com.moongchi.moongchi_be.domain.chat.dto.MessageDto;
import com.moongchi.moongchi_be.domain.chat.service.ChatMessageService;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/chat/rooms/{chatRoomId}/message")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable Long chatRoomId,
            @RequestBody @Valid ChatMessageRequestDto request,
            HttpServletRequest servletRequest
    ) {
        User user = userService.getUser(servletRequest);
        MessageDto dto = chatMessageService.sendMessage(chatRoomId, user.getId(),request);
        return ResponseEntity.status(201).body(dto);
    }

}
