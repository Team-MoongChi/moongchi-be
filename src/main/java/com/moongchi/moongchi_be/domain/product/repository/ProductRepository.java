package com.moongchi.moongchi_be.domain.product.repository;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByCategoryIdIn(List<Long> categoryIds);
    List<Product> findByCategory(Category category);
    List<Product> findByNameContainingIgnoreCase(String name);
}
