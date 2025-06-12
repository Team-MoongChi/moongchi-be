package com.moongchi.moongchi_be.domain.product.service;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.entity.CategoryLevel;
import com.moongchi.moongchi_be.common.category.repository.CategoryRepository;
import com.moongchi.moongchi_be.common.category.service.CategoryService;
import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.favoriite_product.repository.FavoriteProductRepository;
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
    private final FavoriteProductRepository favoriteProductRepository;
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

        int likeCount = getLikeCount(productId);
        return ProductResponseDto.from(product, likeCount);
    }

    public List<Product> searchProducts(String keyword) {
        Category large = categoryRepository.findByNameAndLevel(keyword, CategoryLevel.LARGE).orElse(null);
        if (large != null) {
            List<Long> ids = categoryService.getAllSubCategoryIds(large.getId());
            return productRepository.findByCategoryIdIn(ids);
        }
        Category medium = categoryRepository.findByNameAndLevel(keyword, CategoryLevel.MEDIUM).orElse(null);
        if (medium != null) {
            List<Long> ids = categoryService.getAllSubCategoryIds(medium.getId());
            return productRepository.findByCategoryIdIn(ids);
        }
        Category small = categoryRepository.findByNameAndLevel(keyword, CategoryLevel.SMALL).orElse(null);
        if (small != null) {
            return productRepository.findByCategory(small);
        }
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public int getLikeCount(Long productId){
        return favoriteProductRepository.countByProductId(productId);
    }

}
