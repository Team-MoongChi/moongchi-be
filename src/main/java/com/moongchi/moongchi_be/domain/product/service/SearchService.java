package com.moongchi.moongchi_be.domain.product.service;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.repository.CategoryRepository;
import com.moongchi.moongchi_be.common.category.service.CategoryService;
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
            throw new IllegalArgumentException("검색어는 필수입니다.");
        }

        // 검색 기록 저장
        SearchHistory history = SearchHistory.builder()
                .keyword(keyword)
                .userId(request.getUserId()) // null 가능
                .searchAt(LocalDateTime.now())
                .build();
        searchHistoryRepository.save(history);

        // 1. 카테고리 매칭 시도
        Optional<Category> optionalCategory = categoryRepository.findByName(keyword);
        List<Product> products;

        if (optionalCategory.isPresent()) {
            Long categoryId = optionalCategory.get().getId();
            List<Long> categoryIds = categoryService.getAllSubCategoryIds(categoryId);
            products = productRepository.findByCategoryIdIn(categoryIds);
        } else {
            // fallback: 상품명으로 검색
            products = productRepository.findByNameContainingIgnoreCase(keyword);
        }

        return products.stream()
                .map(ProductResponseDto::from)
                .toList();
    }

    public List<SearchHistoryDto> getSearchHistoriesByUserId(Long userId) {
        List<SearchHistory> histories = searchHistoryRepository.findByUserIdOrderBySearchAtDesc(userId);
        return histories.stream()
                .map(SearchHistoryDto::from)
                .toList();
    }
}
