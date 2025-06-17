package com.moongchi.moongchi_be.domain.product.service;

import com.moongchi.moongchi_be.common.category.entity.Category;
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

import java.util.ArrayList;
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
        Category largeCategory = categoryRepository.findByLargeCategoryAndMediumCategoryIsNullAndSmallCategoryIsNull(product.getCategory().getLargeCategory())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Long largeCategoryId = largeCategory.getId();
        return ProductResponseDto.from(product, likeCount, largeCategoryId);
    }

    public List<Product> searchProducts(String keyword) {
        List<Category> categories = categoryRepository.findAll().stream()
                .filter(c ->
                        keyword.equalsIgnoreCase(c.getLargeCategory()) ||
                                (c.getMediumCategory() != null && keyword.equalsIgnoreCase(c.getMediumCategory())) ||
                                (c.getSmallCategory() != null && keyword.equalsIgnoreCase(c.getSmallCategory()))
                )
                .toList();

        if (!categories.isEmpty()) {
            List<Long> categoryIds = categories.stream().map(Category::getId).toList();
            return productRepository.findByCategoryIdIn(categoryIds);
        }

        return productRepository.findByNameContainingIgnoreCase(keyword);
    }


    public int getLikeCount(Long productId){
        return favoriteProductRepository.countByProductId(productId);
    }

    public List<ProductResponseDto> getProductCategoryList(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        List<Product> products;
        if (category.getMediumCategory() == null && category.getSmallCategory() == null) {
            List<Long> allIds = categoryRepository.findByLargeCategory(category.getLargeCategory())
                    .stream()
                    .map(Category::getId)
                    .toList();
            products = productRepository.findByCategoryIdIn(allIds);
        } else if (category.getSmallCategory() == null) {
            List<Long> allIds = categoryRepository.findByLargeCategoryAndMediumCategory(
                    category.getLargeCategory(), category.getMediumCategory()
            ).stream().map(Category::getId).toList();
            products = productRepository.findByCategoryIdIn(allIds);
        } else {
            products = productRepository.findByCategoryId(categoryId);
        }
        return products.stream().map(ProductResponseDto::from).toList();
    }


    public List<List<ProductResponseDto>> getMainProductList() {
        List<String> largeCategories = categoryRepository.findAllLargeCategories();
        List<List<ProductResponseDto>> result = new ArrayList<>();

        for (String largeCategory : largeCategories) {
            List<Category> categories = categoryRepository.findByLargeCategory(largeCategory);
            List<Long> categoryIds = categories.stream().map(Category::getId).toList();
            List<Product> products = productRepository.findRandom8ByCategoryIds(categoryIds);
            result.add(products.stream().map(ProductResponseDto::from).toList());
        }
        return result;
    }


}
