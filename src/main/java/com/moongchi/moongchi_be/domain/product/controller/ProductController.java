package com.moongchi.moongchi_be.domain.product.controller;

import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardDto;
import com.moongchi.moongchi_be.domain.group_boards.service.GroupBoardService;
import com.moongchi.moongchi_be.domain.product.dto.ProductResponseDto;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import com.moongchi.moongchi_be.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "상품", description = "상품 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final GroupBoardService groupBoardService;

    @Operation(summary = "모든 상품 조회", description = "등록된 모든 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class))))
    })
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        List<ProductResponseDto> productResponseDtos = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDtos);
    }

    @Operation(summary = "단일 상품 조회", description = "productId로 상품 상세정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(
            @Parameter(description = "상품 ID", required = true)
            @PathVariable Long productId) {
        ProductResponseDto product = productService.getProductById(productId);
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @Operation(summary = "상품별 공구글 목록 조회", description = "특정 상품에 연결된 공구글 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupBoardDto.class))))
    })
    @GetMapping("/{productId}/group-boards")
    public ResponseEntity<List<GroupBoardDto>> getProductGroupBoardList(
            @Parameter(description = "상품 ID", required = true)
            @PathVariable Long productId){
        List<GroupBoardDto> groupBoardListDto = groupBoardService.getProductGroupBoardList(productId);
        return ResponseEntity.status(HttpStatus.OK).body(groupBoardListDto);
    }

    @Operation(summary = "상품 검색", description = "키워드(상품명/카테고리명)로 상품을 검색합니다. (대분류/중분류/소분류도 가능)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class))))
    })
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(
            @Parameter(description = "검색 키워드 (상품명 또는 카테고리명)", required = true)
            @RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        List<ProductResponseDto> dtos = products.stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @Operation(summary = "카테고리 별 상품 조회", description = "카테고리 별로 상품을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class))))
    })
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> categoryProducts(@PathVariable Long categoryId){
        List<ProductResponseDto> productResponseDtos = productService.getProductCategoryList(categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDtos);
    }

    @Operation(summary = "쇼핑몰 메인화면", description = "카테고리(대뷴류) 별로 8개씩 랜덤 상품 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class))))
    })
    @GetMapping("/main")
    public ResponseEntity<List<List<ProductResponseDto>>> mainProduct(){
        List<List<ProductResponseDto>> productResponseDtos = productService.getMainProductList();
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDtos);
    }
}
