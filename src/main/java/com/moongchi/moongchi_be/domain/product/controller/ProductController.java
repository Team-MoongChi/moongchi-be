package com.moongchi.moongchi_be.domain.product.controller;

import com.moongchi.moongchi_be.common.category.entity.Category;
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
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        List<ProductResponseDto> dtos = products.stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> categoryProducts(@PathVariable Long categoryId){
        List<ProductResponseDto> productResponseDtos = productService.getProductCategoryList(categoryId);
        return ResponseEntity.ok(productResponseDtos);
    }

    @GetMapping("/main")
    public ResponseEntity<List<List<ProductResponseDto>>> mainProduct(){
        List<List<ProductResponseDto>> productResponseDtos = productService.getMainProductList();
        return ResponseEntity.ok(productResponseDtos);
    }
}
