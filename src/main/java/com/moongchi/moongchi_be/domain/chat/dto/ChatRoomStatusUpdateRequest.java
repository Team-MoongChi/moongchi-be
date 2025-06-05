package com.moongchi.moongchi_be.domain.chat.dto;

import com.moongchi.moongchi_be.domain.chat.entity.ChatRoomStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomStatusUpdateRequest {
    private ChatRoomStatus status;
}
