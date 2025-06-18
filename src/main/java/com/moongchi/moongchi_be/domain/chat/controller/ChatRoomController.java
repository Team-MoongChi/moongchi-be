package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.domain.chat.dto.*;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoom;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoomStatus;
import com.moongchi.moongchi_be.domain.chat.service.ChatRoomService;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채팅", description = "채팅 관련 API")
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @Operation(summary = "내 채팅방 목록 조회", description = "내가 참여한 모든 채팅방(공구) 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<List<ChatRoomResponseDto>> getUserChatRooms(HttpServletRequest request) {
        User currentUser = userService.getUser(request);
        List<ChatRoomResponseDto> chatRooms = chatRoomService.getUserChatRooms(currentUser.getId());
        return ResponseEntity.ok(chatRooms);
    }

    @Operation(summary = "채팅방 상세 조회", description = "채팅방의 상세 정보, 참여자, 메시지 목록을 반환합니다.")
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomDetailDto> getChatRoomDetail(@PathVariable Long chatRoomId, HttpServletRequest request) {
        User currentUser = userService.getUser(request);
        ChatRoomDetailDto dto = chatRoomService.getChatRoomDetail(chatRoomId,currentUser.getId());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "리더의 채팅 상태 변경", description = "구매완료버튼, 리더가 직접 paying-> purchased로 상태를 변경 시킴")
    @PatchMapping("/{chatRoomId}/purchase-complete")
    public ResponseEntity<Void> markAsPurchased(@PathVariable Long chatRoomId, HttpServletRequest request) {
        User user = userService.getUser(request);
        chatRoomService.markAsPurchased(chatRoomId, user.getId());
        return ResponseEntity.ok().build();
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

    @Operation(summary = "공구 결제 완료", description = "채팅방에서 내 결제 상태를 '완료(PAID)'로 변경합니다.")
    @PostMapping("/{chatRoomId}/pay")
    public ResponseEntity<Void> pay (@PathVariable Long chatRoomId,HttpServletRequest request){
        User user = userService.getUser(request);
        chatRoomService.pay(chatRoomId,user.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "거래 완료 처리", description = "현재 채팅방에서 내 참여자 거래 상태를 '완료(TRUE)'로 변경합니다.")
    @PostMapping("/{chatRoomId}/trade-complete")
    public ResponseEntity<Void> tradeComplete(@PathVariable Long chatRoomId, HttpServletRequest request) {
        User user = userService.getUser(request);
        chatRoomService.tradeComplete(chatRoomId,user.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "리뷰 작성", description = "특정 참가자에게 리뷰를 작성합니다.")
    @PostMapping("/{chatRoomId}/reviews/{targetParticipantId}")
    public ResponseEntity<ReviewResponseDto> writeReview(
            @PathVariable Long chatRoomId,
            @PathVariable Long targetParticipantId,
            @RequestBody ReviewRequestDto dto,
            HttpServletRequest request
    ) {
        User currentUser = userService.getUser(request);

        ReviewResponseDto response = chatRoomService.writeReviewByChatRoom(
                chatRoomId, currentUser.getId(), targetParticipantId, dto
        );
        return ResponseEntity.ok(response);
    }

}
