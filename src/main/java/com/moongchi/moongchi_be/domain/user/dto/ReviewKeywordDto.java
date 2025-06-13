package com.moongchi.moongchi_be.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@Schema(description = "사용자 리뷰 키워드 응답 DTO")
public class ReviewKeywordDto {
    private Long userId;
    private List<String> keywords;
}
