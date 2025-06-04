package com.moongchi.moongchi_be.domain.chat.dto;

import com.moongchi.moongchi_be.domain.chat.entity.ChatRoomStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class ChatRoomResponseDto {
    private Long id;
    private String title;
    private ChatRoomStatus status;
    private int participantCount;
    private String imgUrl;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private int unreadCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
