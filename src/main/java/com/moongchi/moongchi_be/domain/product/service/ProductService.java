package com.moongchi.moongchi_be.domain.product.service;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.entity.CategoryLevel;
import com.moongchi.moongchi_be.common.category.repository.CategoryRepository;
import com.moongchi.moongchi_be.common.category.service.CategoryService;
import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.product.dto.ProductResponseDto;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import com.moongchi.moongchi_be.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        return ProductResponseDto.from(product);
    }
    public List<Product> searchProducts(String keyword) {
        // 1. 대분류 찾기
        Category large = categoryRepository.findByNameAndLevel(keyword, CategoryLevel.LARGE).orElse(null);
        if (large != null) {
            List<Long> ids = categoryService.getAllSubCategoryIds(large.getId());
            return productRepository.findByCategoryIdIn(ids);
        }
        // 2. 중분류 찾기
        Category medium = categoryRepository.findByNameAndLevel(keyword, CategoryLevel.MEDIUM).orElse(null);
        if (medium != null) {
            List<Long> ids = categoryService.getAllSubCategoryIds(medium.getId());
            return productRepository.findByCategoryIdIn(ids);
        }
        // 3. 소분류 찾기
        Category small = categoryRepository.findByNameAndLevel(keyword, CategoryLevel.SMALL).orElse(null);
        if (small != null) {
            return productRepository.findByCategory(small);
        }
        // 4. 상품명 fallback
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    }
