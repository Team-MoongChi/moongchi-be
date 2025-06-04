package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.domain.chat.dto.ChatRoomDetailDto;
import com.moongchi.moongchi_be.domain.chat.dto.ChatRoomResponseDto;
import com.moongchi.moongchi_be.domain.chat.dto.ChatRoomStatusUpdateRequest;
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

    @GetMapping
    public ResponseEntity<List<ChatRoomResponseDto>> getAllChatRooms(HttpServletRequest request) {
        User currentUser = userService.getUser(request);

        List<ChatRoomResponseDto> rooms = chatRoomService.getUserChatRooms(currentUser.getId());
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomDetailDto> getChatRoomDetail(@PathVariable Long chatRoomId) {
        ChatRoomDetailDto dto = chatRoomService.getChatRoomDetail(chatRoomId);
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/{chatRoomId}/join")
    public ResponseEntity<String> joinChatRoom(@PathVariable Long chatRoomId, HttpServletRequest request) {
        User currentUser = userService.getUser(request);

        participantService.joinChatRoom(chatRoomId, currentUser.getId());

        return ResponseEntity.ok("채팅방 참여 완료");
    }

    @PostMapping("/{chatRoomId}/pay")
    public ResponseEntity<String> pay(@PathVariable Long chatRoomId, HttpServletRequest request) {
        User currentUser = userService.getUser(request);

        participantService.pay(chatRoomId, currentUser.getId());

        return ResponseEntity.ok("결제가 완료되었습니다.");
    }

    @PostMapping("/{chatRoomId}/trade-complete")
    public ResponseEntity<String> completeTrade(
            @PathVariable Long chatRoomId,
            HttpServletRequest request) {
        User currentUser = userService.getUser(request);

        participantService.completeTrade(chatRoomId, currentUser.getId());

        return ResponseEntity.ok("거래완료 처리되었습니다.");
    }

    @PatchMapping("/{chatRoomId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long chatRoomId,
            @RequestBody ChatRoomStatusUpdateRequest request) {
        chatRoomService.updateChatRoomStatus(chatRoomId,request.getStatus());
        return ResponseEntity.ok("채팅방 상태가 '" + request.getStatus().getKorean() + "'(으)로 변경 되었습니다.");
    }

}
