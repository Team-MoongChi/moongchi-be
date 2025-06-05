package com.moongchi.moongchi_be.domain.product.controller;

import com.moongchi.moongchi_be.domain.product.dto.ProductResponseDto;
import com.moongchi.moongchi_be.domain.product.dto.ProductSearchRequest;
import com.moongchi.moongchi_be.domain.product.dto.SearchHistoryDto;
import com.moongchi.moongchi_be.domain.product.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<List<ProductResponseDto>> search(@RequestBody ProductSearchRequest request) {
        List<ProductResponseDto> result = searchService.searchProducts(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<SearchHistoryDto>> getUserSearchHistory(@PathVariable Long userId) {
        List<SearchHistoryDto> histories = searchService.getSearchHistoriesByUserId(userId);
        return ResponseEntity.ok(histories);
    }

}
