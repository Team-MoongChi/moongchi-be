package com.moongchi.moongchi_be.domain.favoriite_product.controller;

import com.moongchi.moongchi_be.domain.favoriite_product.service.FavoriteProductService;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.product.dto.ProductResponseDto;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
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

    @PostMapping("/group-boards/{group_board_id}/like")
    public ResponseEntity<?> addLike(@PathVariable("group_board_id") Long groupBoardId, HttpServletRequest request){
        User user  = userService.getUser(request);
        favoriteProductService.addLike(groupBoardId, user);
        return ResponseEntity.ok("찜 추가 완료");
    }

    @DeleteMapping("/group-boards/{group_board_id}/like")
    public ResponseEntity<?> removeLike(@PathVariable("group_board_id") Long groupBoardId, HttpServletRequest request){
        User user  = userService.getUser(request);
        favoriteProductService.removeLike(groupBoardId, user);
        return ResponseEntity.ok("찜 삭제 완료");
    }

    @GetMapping("/group-boards/like")
    public ResponseEntity<List<GroupBoardListDto>> getLikes(HttpServletRequest request){
        User user  = userService.getUser(request);
        List<GroupBoardListDto> groupBoardListDtos = favoriteProductService.getLikes(user);
        return ResponseEntity.ok(groupBoardListDtos);
    }

    @PostMapping("/products/{product_id}/like")
    public ResponseEntity<?> addProductLike(@PathVariable("product_id") Long productId, HttpServletRequest request){
        User user  = userService.getUser(request);
        favoriteProductService.addProductLike(productId, user);
        return ResponseEntity.ok("찜 추가 완료");
    }

    @DeleteMapping("/products/{product_id}/like")
    public ResponseEntity<?> removeProductLike(@PathVariable("product_id") Long productId, HttpServletRequest request){
        User user  = userService.getUser(request);
        favoriteProductService.removeProductLike(productId, user);
        return ResponseEntity.ok("찜 삭제 완료");
    }

    @GetMapping("/products/like")
    public ResponseEntity<List<ProductResponseDto>> getProductLikes(HttpServletRequest request){
        User user  = userService.getUser(request);
        List<ProductResponseDto> productResponseDtoList = favoriteProductService.getProductLikes(user);
        return ResponseEntity.ok(productResponseDtoList);
    }


}
