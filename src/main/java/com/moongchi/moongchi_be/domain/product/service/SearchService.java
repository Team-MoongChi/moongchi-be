package com.moongchi.moongchi_be.domain.product.service;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.repository.CategoryRepository;
import com.moongchi.moongchi_be.common.category.service.CategoryService;
import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.product.dto.ProductResponseDto;
import com.moongchi.moongchi_be.domain.product.dto.ProductSearchRequest;
import com.moongchi.moongchi_be.domain.product.dto.SearchHistoryDto;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import com.moongchi.moongchi_be.domain.product.entity.SearchHistory;
import com.moongchi.moongchi_be.domain.product.repository.ProductRepository;
import com.moongchi.moongchi_be.domain.product.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;

    public List<ProductResponseDto> searchProducts(ProductSearchRequest request) {
        String keyword = request.getKeyword();
        if (keyword == null || keyword.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        SearchHistory history = SearchHistory.builder()
                .keyword(keyword)
                .userId(request.getUserId()) // null 가능
                .searchAt(LocalDateTime.now())
                .build();
        searchHistoryRepository.save(history);

        Optional<Category> optionalCategory = categoryRepository.findByName(keyword);
        List<Product> products;

        if (optionalCategory.isPresent()) {
            Long categoryId = optionalCategory.get().getId();
            List<Long> categoryIds = categoryService.getAllSubCategoryIds(categoryId);
            products = productRepository.findByCategoryIdIn(categoryIds);
        } else {
            products = productRepository.findByNameContainingIgnoreCase(keyword);
        }

        return products.stream()
                .map(ProductResponseDto::from)
                .toList();
    }

    public List<SearchHistoryDto> getSearchHistoriesByUserId(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        List<SearchHistory> histories = searchHistoryRepository.findByUserIdOrderBySearchAtDesc(userId);
        return histories.stream()
                .map(SearchHistoryDto::from)
                .toList();
    }
}
