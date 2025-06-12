package com.moongchi.moongchi_be.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "상품 검색 요청 DTO")
public class ProductSearchRequest {
    @Schema(description = "검색 키워드 (상품명 또는 카테고리명)")
    private String keyword;
    @Schema(description = "요청하는 유저의 ID(선택)")
    private Long userId;
}
