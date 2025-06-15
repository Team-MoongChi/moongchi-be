package com.moongchi.moongchi_be.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDto {
    private Long chatRoomId;
    private Long participantId;
    @NotBlank(message = "메시지를 입력해주세요.")
    private String message;
    private String messageType;

}

