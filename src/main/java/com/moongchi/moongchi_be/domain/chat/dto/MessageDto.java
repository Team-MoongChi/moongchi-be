package com.moongchi.moongchi_be.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
public class MessageDto {
    private String id;
    private Long participantId;
    private String message;
    private String messageType;
    private LocalDateTime sendAt;

}