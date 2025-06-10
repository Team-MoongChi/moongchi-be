package com.moongchi.moongchi_be.domain.product.controller;

import com.moongchi.moongchi_be.common.log.LogEvent;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardDto;
import com.moongchi.moongchi_be.domain.group_boards.service.GroupBoardService;
import com.moongchi.moongchi_be.domain.product.dto.ProductResponseDto;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import com.moongchi.moongchi_be.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final GroupBoardService groupBoardService;
    @GetMapping
    public List<ProductResponseDto> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{productId}")
    @LogEvent("click")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId) {
        ProductResponseDto product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{productId}/group-boards")
    public ResponseEntity<List<GroupBoardDto>> getProductGroupBoardList(@PathVariable Long productId){
        List<GroupBoardDto> groupBoardListDto = groupBoardService.getProductGroupBoardList(productId);
        return ResponseEntity.ok(groupBoardListDto);
    }

    @GetMapping("/search")
    @LogEvent("search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        List<ProductResponseDto> dtos = products.stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

}
