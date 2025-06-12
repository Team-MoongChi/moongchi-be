package com.moongchi.moongchi_be.domain.favoriite_product.controller;

import com.moongchi.moongchi_be.domain.favoriite_product.service.FavoriteProductService;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.product.dto.ProductResponseDto;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관심상품", description = "관심상품 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FavoriteProductController {

    private final FavoriteProductService favoriteProductService;
    private final UserService userService;

    @Operation(summary = "공구글 찜 추가", description = "공구글에 찜을 추가합니다.")
    @ApiResponse(responseCode = "200", description = "찜 추가 완료")
    @PostMapping("/group-boards/{group_board_id}/like")
    public ResponseEntity<?> addLike(@PathVariable("group_board_id") Long groupBoardId, HttpServletRequest request){
        User user  = userService.getUser(request);
        favoriteProductService.addLike(groupBoardId, user);
        return ResponseEntity.ok("찜 추가 완료");
    }

    @Operation(
            summary = "공구글 찜(좋아요) 삭제",
            description = "특정 공구글(group_board_id)의 찜을 삭제합니다."
    )
    @ApiResponse(responseCode = "200", description = "찜 삭제 완료")
    @DeleteMapping("/group-boards/{group_board_id}/like")
    public ResponseEntity<?> removeLike(@PathVariable("group_board_id") Long groupBoardId, HttpServletRequest request){
        User user  = userService.getUser(request);
        favoriteProductService.removeLike(groupBoardId, user);
        return ResponseEntity.ok("찜 삭제 완료");
    }

    @Operation(
            summary = "내가 찜한 공구글 목록 조회",
            description = "내가 찜한 모든 공구글(공동구매 게시글) 목록을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/group-boards/like")
    public ResponseEntity<List<GroupBoardListDto>> getLikes(HttpServletRequest request){
        User user  = userService.getUser(request);
        List<GroupBoardListDto> groupBoardListDtos = favoriteProductService.getLikes(user);
        return ResponseEntity.ok(groupBoardListDtos);
    }

    @Operation(
            summary = "상품 찜(좋아요) 추가",
            description = "특정 상품(product_id)에 찜을 추가합니다."
    )
    @ApiResponse(responseCode = "200", description = "찜 추가 완료")
    @PostMapping("/products/{product_id}/like")
    public ResponseEntity<?> addProductLike(@PathVariable("product_id") Long productId, HttpServletRequest request){
        User user  = userService.getUser(request);
        favoriteProductService.addProductLike(productId, user);
        return ResponseEntity.ok("찜 추가 완료");
    }

    @Operation(
            summary = "상품 찜(좋아요) 삭제",
            description = "특정 상품(product_id)의 찜을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "찜 삭제 완료")
    })
    @DeleteMapping("/products/{product_id}/like")
    public ResponseEntity<?> removeProductLike(@PathVariable("product_id") Long productId, HttpServletRequest request){
        User user  = userService.getUser(request);
        favoriteProductService.removeProductLike(productId, user);
        return ResponseEntity.ok("찜 삭제 완료");
    }

    @Operation(
            summary = "내가 찜한 상품 목록 조회",
            description = "내가 찜한 모든 상품(Product) 목록을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/products/like")
    public ResponseEntity<List<ProductResponseDto>> getProductLikes(HttpServletRequest request){
        User user  = userService.getUser(request);
        List<ProductResponseDto> productResponseDtoList = favoriteProductService.getProductLikes(user);
        return ResponseEntity.ok(productResponseDtoList);
    }


}
