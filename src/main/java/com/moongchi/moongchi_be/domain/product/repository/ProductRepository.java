package com.moongchi.moongchi_be.domain.product.repository;

import com.moongchi.moongchi_be.domain.product.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByCategoryIdIn(List<Long> categoryIds);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String name);
    @Query(value = """
    SELECT *
    FROM products p
    WHERE p.category_id IN (:categoryIds)
    ORDER BY RAND()
    LIMIT 8
    """, nativeQuery = true)
    List<Product> findRandom8ByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds ORDER BY p.id DESC")
    List<Product> findTopNByCategoryIdsOrderByIdDesc(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds AND p.id < :lastId ORDER BY p.id DESC")
    List<Product> findTopNByCategoryIdsAndIdLessThanOrderByIdDesc(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

}
