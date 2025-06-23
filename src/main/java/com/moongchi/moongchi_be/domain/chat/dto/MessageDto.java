package com.moongchi.moongchi_be.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moongchi.moongchi_be.domain.chat.entity.ChatMessage;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@Builder
@Schema(description = "채팅 메시지")
public class MessageDto {
    @Schema(description = "메시지 ID")
    private String id;
    @Schema(description = "참여자 ID(시스템메시지는 null)")
    private Long participantId;
    @Schema(description = "내용")
    private String message;
    @Schema(description = "메시지타입 (USER/SYSTEM 등)")
    private String messageType;
    @Schema(description = "전송시각")
    private LocalDateTime sendAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "(시스템메세지) 채팅 상태")
    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "버튼 타입 (예: PURCHASE_COMPLETE, TRADE_COMPLETE)")
    private String chatStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "버튼이 보일 대상 (예: LEADER, PARTICIPANT, ALL)")
    private String buttonVisibleTo;

    public static MessageDto from(ChatMessage message) {
        return MessageDto.builder()
                .id(message.getId())
                .participantId(message.getParticipantId())
                .message(message.getMessage())
                .messageType(message.getMessageType().name())
                .sendAt(message.getSendAt())
                .build();
    }

    public static MessageDto from(ChatMessage message, ChatRoomStatus status, String chatStatus, String buttonVisibleTo) {
        return MessageDto.builder()
                .id(message.getId())
                .participantId(message.getParticipantId())
                .message(message.getMessage())
                .messageType(message.getMessageType().name())
                .sendAt(message.getSendAt())
                .status(status != null ? status.getKorean() : null)
                .chatStatus(chatStatus)
                .buttonVisibleTo(buttonVisibleTo)
                .build();
    }
    public static MessageDto ofEnter(ParticipantDto p) {
        return MessageDto.builder()
                .id(null)
                .participantId(p.getUserId())
                .message(p.getNickname() + "님이 입장했습니다")
                .messageType("ENTER")
                .sendAt(p.getJoinAt())
                .build();
    }
}