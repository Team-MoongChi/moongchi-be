package com.moongchi.moongchi_be.domain.group_boards.dto;

import com.moongchi.moongchi_be.common.category.dto.CategoryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupProductDto {
    private Long id;
    private String name;
    private int price;
    private String quantity;
    private List<String> images;
    private CategoryResponseDto category;
}
