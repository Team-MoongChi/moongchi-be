package com.moongchi.moongchi_be.common.category.dto;

import com.moongchi.moongchi_be.common.category.entity.CategoryLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리명")
public class CategoryResponseDto {
    @Schema(description = "카테고리 ID")
    private Long id;
    @Schema(description = "카테고리 이름")
    private String name;
    @Schema(description = "카테고리 종류(대분류>중분류>소분류)")
    private CategoryLevel level;
    @Schema(description = "참여자 ID")
    private Long parentId;
    @Schema(description = "하위 카테고리(대분류>중분류>소분류)")
    private List<CategoryResponseDto> subCategories;

}
