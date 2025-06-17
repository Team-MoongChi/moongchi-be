package com.moongchi.moongchi_be.domain.product.dto;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 응답 DTO")
public class ProductResponseDto {

    @Schema(description = "상품 고유 ID")
    private Long id;

    @Schema(description = "상품 이름")
    private String name;

    @Schema(description = "상품 가격")
    private int price;

    @Schema(description = "상품 이미지 URL")
    private String imgUrl;

    @Schema(description = "상품 상세 페이지 URL")
    private String productUrl;

    @Schema(description = "상품 평점 (0.0~5.0)")
    private Double rating;

    @Schema(description = "추천수")
    private Integer likeCount;

    @Schema(description = "대분류 카테고리 Id")
    private Long largeCategoryId;

    @Schema(description = "대분류 카테고리")
    private String largeCategory;

    @Schema(description = "중분류 카테고리")
    private String mediumCategory;

    @Schema(description = "소분류 카테고리")
    private String smallCategory;


    public static ProductResponseDto from(Product product) {
        Category category = product.getCategory();
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImgUrl(),
                product.getProductUrl(),
                product.getRating(),
                null,
                null,
                category != null ? category.getLargeCategory() : null,
                category != null ? category.getMediumCategory() : null,
                category != null ? category.getSmallCategory() : null
        );
    }

    public static ProductResponseDto from(Product product, int likeCount, Long largeCategoryId) {
        Category category = product.getCategory();
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImgUrl(),
                product.getProductUrl(),
                product.getRating(),
                likeCount,
                largeCategoryId,
                category != null ? category.getLargeCategory() : null,
                category != null ? category.getMediumCategory() : null,
                category != null ? category.getSmallCategory() : null
        );
    }
}
