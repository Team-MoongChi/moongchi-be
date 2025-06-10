package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.domain.chat.dto.ChatRoomDetailDto;
import com.moongchi.moongchi_be.domain.chat.dto.ChatRoomResponseDto;
import com.moongchi.moongchi_be.domain.chat.dto.ChatRoomStatusResponse;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoom;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoomStatus;
import com.moongchi.moongchi_be.domain.chat.service.ChatRoomService;
import com.moongchi.moongchi_be.domain.chat.service.ParticipantService;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ParticipantService participantService;
    private final UserService userService;

    //채팅 목록 조회
    @GetMapping
    public ResponseEntity<List<ChatRoomResponseDto>> getUserChatRooms(HttpServletRequest request) {
        User currentUser = userService.getUser(request);
        List<ChatRoomResponseDto> chatRooms = chatRoomService.getUserChatRooms(currentUser.getId());
        return ResponseEntity.ok(chatRooms);
    }

    //채팅방 상세 조회
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomDetailDto> getChatRoomDetail(@PathVariable Long chatRoomId, HttpServletRequest request) {
        User currentUser = userService.getUser(request);
        ChatRoomDetailDto dto = chatRoomService.getChatRoomDetail(chatRoomId,currentUser.getId());
        return ResponseEntity.ok(dto);
    }
    
    //채팅 진행 상태 변경
    @PatchMapping("/{chatRoomId}/status")
    public ResponseEntity<ChatRoomStatusResponse> updateChatRoomStatus(@PathVariable Long chatRoomId) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        ChatRoomStatus before = chatRoom.getStatus();
        ChatRoomStatus after = chatRoomService.updateChatRoomStatus(chatRoomId);

        ChatRoomStatusResponse response = ChatRoomStatusResponse.builder()
                .chatRoomId(chatRoomId)
                .previousStatus(before)
                .newStatus(after)
                .message("채팅방 상태가 업데이트되었습니다.")
                .build();

        return ResponseEntity.ok(response);
    }

}
