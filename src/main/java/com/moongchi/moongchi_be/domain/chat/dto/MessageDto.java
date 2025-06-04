package com.moongchi.moongchi_be.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@Builder
public class MessageDto {
    private String id;
    private String userId;
    private String message;
    private String messageType;
    private LocalDateTime sendAt;
}