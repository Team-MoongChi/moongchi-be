package com.moongchi.moongchi_be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Schema(description = "리뷰 작성 요청")
public class ReviewRequestDto {
    @NotNull
    @DecimalMin("0.5")
    @DecimalMax("5.0")
    @Schema(description = "별점(0.5~5.0)")
    private Double star;
    @Schema(description = "키워드 리스트(쉼표 구분)")
    @NotBlank
    private List<String> keywords;
    @Schema(description = "리뷰내용")
    private String review;
}
