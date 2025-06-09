package com.moongchi.moongchi_be.domain.product.controller;

import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.group_boards.service.GroupBoardService;
import com.moongchi.moongchi_be.domain.product.dto.ProductResponseDto;
import com.moongchi.moongchi_be.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    private final GroupBoardService groupBoardService;
    @GetMapping
    public List<ProductResponseDto> getAll() {
        return service.getAllProducts();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId) {
        ProductResponseDto product = service.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{productId}/group-boards")
    public ResponseEntity<List<GroupBoardDto>> getProductGroupBoardList(@PathVariable Long productId){
        List<GroupBoardDto> groupBoardListDto = groupBoardService.getProductGroupBoardList(productId);
        return ResponseEntity.ok(groupBoardListDto);
    }


}
