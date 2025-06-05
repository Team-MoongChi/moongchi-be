package com.moongchi.moongchi_be.domain.product.dto;

import com.moongchi.moongchi_be.common.category.dto.CategoryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String imgUrl;
    private String productUrl;
}
