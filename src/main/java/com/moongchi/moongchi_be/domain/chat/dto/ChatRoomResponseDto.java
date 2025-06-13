package com.moongchi.moongchi_be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@Schema(description = "채팅방 리스트 응답")
public class ChatRoomResponseDto {
    @Schema(description = "채팅방 ID")
    private Long id;
    @Schema(description = "채팅방 제목")
    private String title;
    @Schema(description = "채팅방 상태")
    private String status;
    @Schema(description = "참여자 수")
    private int participantCount;
    @Schema(description = "대표 이미지 URL")
    private String imgUrl;
    @Schema(description = "마지막 메시지")
    private String lastMessage;
    @Schema(description = "마지막 메시지 전송 시간")
    private LocalDateTime lastMessageTime;
    @Schema(description = "읽지 않은 메시지 수")
    private int unreadCount;
}
