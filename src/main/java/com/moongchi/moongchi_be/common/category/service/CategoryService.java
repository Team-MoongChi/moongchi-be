package com.moongchi.moongchi_be.common.category.service;

import com.moongchi.moongchi_be.common.category.dto.CategoryResponseDto;
import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponseDto::new)
                .toList();
    }


    public List<CategoryResponseDto> getLargeCategories() {
        // 대분류만 뽑아서 반환
        List<String> largeCategories = categoryRepository.findAllLargeCategories();
        // 실제 대분류 카테고리 row 목록 반환
        return categoryRepository.findAll().stream()
                .filter(c -> c.getMediumCategory() == null && c.getSmallCategory() == null)
                .map(CategoryResponseDto::new)
                .toList();
    }

    public Long getCategoryId(String large, String medium, String small) {
        Category c = categoryRepository.findByLargeCategoryAndMediumCategoryAndSmallCategory(large, medium, small)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리 없음"));
        return c.getId();
    }

}
