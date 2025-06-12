package com.moongchi.moongchi_be.domain.favoriite_product.controller;

import com.moongchi.moongchi_be.domain.favoriite_product.service.FavoriteProductService;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FavoriteProductController {

    private final FavoriteProductService favoriteProductService;

    @PostMapping("/group-boards/{group_board_id}/like")
    public ResponseEntity<?> addLike(@PathVariable("group_board_id") Long groupBoardId, HttpServletRequest request){
        favoriteProductService.addLike(groupBoardId, request);
        return ResponseEntity.ok("찜 추가 완료");
    }

    @DeleteMapping("/group-boards/{group_board_id}/like")
    public ResponseEntity<?> removeLike(@PathVariable("group_board_id") Long groupBoardId, HttpServletRequest request){
        favoriteProductService.removeLike(groupBoardId, request);
        return ResponseEntity.ok("찜 삭제 완료");
    }

    @GetMapping("/group-boards/like")
    public ResponseEntity<List<GroupBoardListDto>> getLikes(HttpServletRequest request){
        List<GroupBoardListDto> groupBoardListDtos = favoriteProductService.getLikes(request);
        return ResponseEntity.ok(groupBoardListDtos);
    }
}
