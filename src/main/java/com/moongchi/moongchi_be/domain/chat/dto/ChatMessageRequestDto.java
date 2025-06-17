package com.moongchi.moongchi_be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅 메시지 요청")
public class ChatMessageRequestDto {
    @Schema(description = "채팅방 ID")
    private Long chatRoomId;
    @NotBlank(message = "메시지를 입력해주세요.")
    @Schema(description = "채팅 메시지")
    private String message;
    @Schema(description = "메세지 타입 (system/text)")
    private String messageType;
}

