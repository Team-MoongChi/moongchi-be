package com.moongchi.moongchi_be.common.category.service;

import com.moongchi.moongchi_be.common.category.dto.CategoryResponseDto;
import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.entity.CategoryLevel;
import com.moongchi.moongchi_be.common.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> topCategories = categoryRepository.findByLevel(CategoryLevel.LARGE);

        return topCategories.stream()
                .map(this::toDto)
                .toList();
    }

    private CategoryResponseDto toDto(Category category) {
        List<CategoryResponseDto> subCategories = category.getSubCategories().stream()
                .map(this::toDto)
                .toList();

        Long parentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;

        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getLevel(),
                parentId,
                subCategories
        );
    }

    @Transactional(readOnly = true)
    public List<Long> getAllSubCategoryIds(Long categoryId) {
        Category root = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        List<Long> categoryIds = new ArrayList<>();
        collectCategoryIds(root,categoryIds);
        return categoryIds;
    }

    private void collectCategoryIds(Category category, List<Long> ids) {
        ids.add(category.getId());
        for (Category sub : category.getSubCategories()) {
            collectCategoryIds(sub,ids);
        }
    }
}
