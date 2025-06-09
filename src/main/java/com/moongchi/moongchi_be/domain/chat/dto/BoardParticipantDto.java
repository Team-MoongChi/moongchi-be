package com.moongchi.moongchi_be.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
@Builder
public class BoardParticipantDto {
    private Long id;
    private Long userId;
    private String nickname;
    private String profileUrl;
    private String role;
    private Double mannerLeader;

}
