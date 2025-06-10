package com.moongchi.moongchi_be.domain.product.dto;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private Long id;
    private String name;
    private int price;
    private String imgUrl;
    private String productUrl;
    private Double rating;

    private Long categoryId;
    private String largeCategory;
    private String mediumCategory;
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
                product.getCategory().getId(),
                getCategoryNameByLevel(product.getCategory(), "LARGE"),
                getCategoryNameByLevel(product.getCategory(), "MEDIUM"),
                getCategoryNameByLevel(product.getCategory(), "SMALL")
        );
    }

}
