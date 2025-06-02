package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.domain.chat.dto.ChatRoomResponse;
import com.moongchi.moongchi_be.domain.chat.dto.ChatRoomStatusUpdateRequest;
import com.moongchi.moongchi_be.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getAllChatRooms() {
        List<ChatRoomResponse> chatRooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(chatRooms);
    }

    @PatchMapping("/{chatRoomId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long chatRoomId,
            @RequestBody ChatRoomStatusUpdateRequest request) {
        chatRoomService.updateChatRoomStatus(chatRoomId,request.getStatus());
        return ResponseEntity.ok("채팅방 상태가 '" + request.getStatus().getKorean() + "'(으)로 변경 되었습니다.");
    }

}
