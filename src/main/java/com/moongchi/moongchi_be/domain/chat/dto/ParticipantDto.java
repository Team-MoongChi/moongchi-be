package com.moongchi.moongchi_be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter @Getter
@AllArgsConstructor
@Builder
@Schema(description = "참여자 정보")
public class ParticipantDto {
    @Schema(description = "참여자ID")
    private Long participantId;
    @Schema(description = "유저ID")
    private Long userId;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "프로필 이미지")
    private String profileUrl;
    @Schema(description = "역할(LEADER/MEMBER)")
    private String role;
    @Schema(description = "결제 상태(PAID/UNPAID)")
    private String payStatement;
    @Schema(description = "입장 시간")
    private LocalDateTime joinAt;
    @Schema(description = "거래완료 여부")
    private boolean tradeCompleted;
    @Schema(description = "n분의 1 금액")
    private int perPersonPrice;
    @Schema(description = "나인가?")
    private boolean isMe;
    @Schema(description = "리뷰 완료 여부")
    private boolean reviewed;

}
