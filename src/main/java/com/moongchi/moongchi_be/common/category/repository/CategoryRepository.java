package com.moongchi.moongchi_be.common.category.repository;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.entity.CategoryLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> findByLevel(CategoryLevel level);
    Optional<Category> findByNameAndLevel(String name, CategoryLevel level);
}
