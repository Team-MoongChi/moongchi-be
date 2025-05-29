package com.moongchi.moongchi_be.common.category.repository;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.entity.CategoryLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> findByLevel(CategoryLevel level);
}
