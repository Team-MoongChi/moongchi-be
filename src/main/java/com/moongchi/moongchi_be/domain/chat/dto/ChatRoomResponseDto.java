package com.moongchi.moongchi_be.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class ChatRoomResponseDto {
    private Long id;
    private String title;
    private String status;
    private int participantCount;
    private String imgUrl;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private int unreadCount;

}
