package com.moongchi.moongchi_be.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
@Builder
public class ParticipantDto {
    private Long participantId;
    private Long userId;
    private String nickname;
    private String profileUrl;
    private String role;
    private String payStatement;
    private boolean tradeCompleted;
    private int perPersonPrice;
    private boolean isMe;
    private boolean reviewed;

}
