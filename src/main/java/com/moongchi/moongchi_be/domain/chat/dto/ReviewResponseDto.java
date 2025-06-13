package com.moongchi.moongchi_be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@Schema(description = "리뷰 작성 응답")
public class ReviewResponseDto {
    @Schema(description = "리뷰 ID")
    private Long id;
    @Schema(description = "별점")
    private Double star;
    @Schema(description = "키워드 리스트")
    private List<String> keywords;
    @Schema(description = "리뷰내용")
    private String  review;
    @Schema(description = "리뷰대상 참가자 ID")
    private Long participantId;
    @Schema(description = "공구글 ID")
    private Long groupBoardId;
    @Schema(description = "작성일시")
    private LocalDateTime createdAt;
}
