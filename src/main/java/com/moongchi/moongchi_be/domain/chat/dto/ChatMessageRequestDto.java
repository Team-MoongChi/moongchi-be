package com.moongchi.moongchi_be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅 메시지 요청")
public class ChatMessageRequestDto {
    @NotBlank(message = "메시지를 입력해주세요.")
    @Schema(description = "채팅 메시지")
    private String message;
}

