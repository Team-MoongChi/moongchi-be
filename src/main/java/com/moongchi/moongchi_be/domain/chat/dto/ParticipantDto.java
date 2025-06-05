package com.moongchi.moongchi_be.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ParticipantDto {
    private Long id;
    private Long userId;
    private String nickname;
    private String profileUrl;
    private String role;
    private Double mannerLeader;

    @Builder
    public ParticipantDto(Long id, Long userId, String nickname, String profileUrl, String role, Double mannerLeader) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.role = role;
        this.mannerLeader = mannerLeader;
    }

    @Builder
    public ParticipantDto(Long userId,String nickname, String profileUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }
}
