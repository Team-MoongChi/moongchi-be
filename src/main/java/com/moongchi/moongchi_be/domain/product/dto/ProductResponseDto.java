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

    @Schema(description = "상품 고유 ID", example = "1")
    private Long id;

    @Schema(description = "상품 이름", example = "유기농 사과 2kg")
    private String name;

    @Schema(description = "상품 가격", example = "9900")
    private int price;

    @Schema(description = "상품 이미지 URL", example = "https://example.com/apple.jpg")
    private String imgUrl;

    @Schema(description = "상품 상세 페이지 URL", example = "https://example.com/product/1")
    private String productUrl;

    @Schema(description = "상품 평점 (0.0~5.0)", example = "4.5")
    private Double rating;

    @Schema(description = "대분류 카테고리", example = "신선식품")

    private Integer likeCount;

    private String largeCategory;

    @Schema(description = "중분류 카테고리", example = "과일")
    private String mediumCategory;

    @Schema(description = "소분류 카테고리", example = "사과")
    private String smallCategory;


    private static String getCategoryNameByLevel(Category category, String targetLevel) {
        while (category != null) {
            if (category.getLevel().name().equalsIgnoreCase(targetLevel)) {
                return category.getName();
            }
            category = category.getParentCategory();
        }
        return null;
    }

    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImgUrl(),
                product.getProductUrl(),
                product.getRating(),
                null,
                getCategoryNameByLevel(product.getCategory(), "LARGE"),
                getCategoryNameByLevel(product.getCategory(), "MEDIUM"),
                getCategoryNameByLevel(product.getCategory(), "SMALL")
        );
    }


    public static ProductResponseDto from(Product product, int likeCount) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImgUrl(),
                product.getProductUrl(),
                product.getRating(),
                likeCount,
                getCategoryNameByLevel(product.getCategory(), "LARGE"),
                getCategoryNameByLevel(product.getCategory(), "MEDIUM"),
                getCategoryNameByLevel(product.getCategory(), "SMALL")
        );
    }

}
