package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.domain.chat.dto.ChatMessageRequestDto;
import com.moongchi.moongchi_be.domain.chat.dto.MessageDto;
import com.moongchi.moongchi_be.domain.chat.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/chat/rooms/{chatRoomId}/message")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable Long chatRoomId,
            @RequestBody @Valid ChatMessageRequestDto request
    ) {
        MessageDto dto = chatMessageService.sendMessage(chatRoomId, request);
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(
            @PathVariable Long chatRoomId
    ) {
        List<MessageDto> list = chatMessageService.getMessages(chatRoomId);
        return ResponseEntity.ok(list);
    }

}
