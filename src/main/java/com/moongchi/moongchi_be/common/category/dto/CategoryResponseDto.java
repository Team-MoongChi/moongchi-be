package com.moongchi.moongchi_be.common.category.dto;

import com.moongchi.moongchi_be.common.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리명")
public class CategoryResponseDto {
    @Schema(description = "카테고리 ID")
    private Long id;
    @Schema(description = "대분류 카테고리")
    private String largeCategory;
    @Schema(description = "중분류 카테고리")
    private String mediumCategory;
    @Schema(description = "소분류 카테고리")
    private String smallCategory;
    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.largeCategory = category.getLargeCategory();
        this.mediumCategory = category.getMediumCategory();
        this.smallCategory = category.getSmallCategory();
    }
}
