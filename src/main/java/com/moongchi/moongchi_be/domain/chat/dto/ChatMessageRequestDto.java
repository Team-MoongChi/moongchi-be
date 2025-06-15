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
    private Long chatRoomId;
    private Long participantId;
    @NotBlank(message = "메시지를 입력해주세요.")
    @Schema(description = "채팅 메시지")
    private String message;
    private String messageType;

}

