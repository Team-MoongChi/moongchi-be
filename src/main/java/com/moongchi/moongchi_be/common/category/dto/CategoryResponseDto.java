package com.moongchi.moongchi_be.common.category.dto;

import com.moongchi.moongchi_be.common.category.entity.CategoryLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {

    private Long id;
    private String name;
    private CategoryLevel level;
    private Long parentId;
    private List<CategoryResponseDto> subCategories;

}
