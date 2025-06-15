package com.moongchi.moongchi_be.common.category.repository;

import com.moongchi.moongchi_be.common.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    // 대분류만
    List<Category> findByLargeCategory(String largeCategory);

    // 대+중분류
    List<Category> findByLargeCategoryAndMediumCategory(String largeCategory, String mediumCategory);

    // 대+중+소
    Optional<Category> findByLargeCategoryAndMediumCategoryAndSmallCategory(String large, String medium, String small);

    // 전체 대분류 목록
    @Query("SELECT DISTINCT c.largeCategory FROM Category c")
    List<String> findAllLargeCategories();
}