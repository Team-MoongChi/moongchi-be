package com.moongchi.moongchi_be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
@Builder
@Schema(description = "공구방(채팅) 참여자")
public class BoardParticipantDto {
    @Schema(description = "공구글 ID")
    private Long id;
    @Schema(description = "유저 ID")
    private Long userId;
    @Schema(description = "유저 nickname")
    private String nickname;
    @Schema(description = "유저의 프로필 이미지 url")
    private String profileUrl;
    @Schema(description = "유저의 역할")
    private String role;
    @Schema(description = "유저 리더온도")
    private Double mannerLeader;

}
