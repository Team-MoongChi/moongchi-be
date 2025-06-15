package com.moongchi.moongchi_be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
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

}