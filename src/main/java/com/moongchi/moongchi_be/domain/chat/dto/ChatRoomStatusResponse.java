package com.moongchi.moongchi_be.domain.chat.dto;

import com.moongchi.moongchi_be.domain.chat.entity.ChatRoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomStatusResponse {
    @Schema(description = "채팅방 ID")
    private Long chatRoomId;
    @Schema(description = "이전 채팅방 상태")
    private ChatRoomStatus previousStatus;
    @Schema(description = "새로운 채팅방 상태")
    private ChatRoomStatus newStatus;
    @Schema(description = "채팅방 상태가 업데이트 되었습니다.")
    private String message;
}
