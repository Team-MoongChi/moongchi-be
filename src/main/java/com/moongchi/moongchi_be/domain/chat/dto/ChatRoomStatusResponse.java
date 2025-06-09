package com.moongchi.moongchi_be.domain.chat.dto;

import com.moongchi.moongchi_be.domain.chat.entity.ChatRoomStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomStatusResponse {
    private Long chatRoomId;
    private ChatRoomStatus previousStatus;
    private ChatRoomStatus newStatus;
    private String message;
}
