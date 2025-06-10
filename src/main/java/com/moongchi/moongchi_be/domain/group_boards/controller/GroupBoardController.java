package com.moongchi.moongchi_be.domain.group_boards.controller;

import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardRequestDto;
import com.moongchi.moongchi_be.domain.group_boards.service.GroupBoardService;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group-boards")
public class GroupBoardController {

    private final GroupBoardService groupBoardService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "공동구매 게시글")
    public ResponseEntity<?> createPost(@RequestBody GroupBoardRequestDto dto, HttpServletRequest request) {
        groupBoardService.createPost(dto, request);
        return ResponseEntity.ok("게시글이 추가되었습니다.");
    }

    @PutMapping("/{group_board_id}")
    public ResponseEntity<?> updatePost(@PathVariable("group_board_id") Long groupBoardId, @RequestBody GroupBoardRequestDto dto) {
        groupBoardService.updatePost(groupBoardId, dto);
        return ResponseEntity.ok("게시글이 수정되었습니다.");
    }

    @DeleteMapping("/{group_board_id}")
    public ResponseEntity<?> deletePost(@PathVariable("group_board_id") Long groupBoardId) {
        groupBoardService.deletePost(groupBoardId);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    @GetMapping
    public ResponseEntity<List<GroupBoardListDto>> getGroupBoardList(HttpServletRequest request){
        List<GroupBoardListDto> groupBoards = groupBoardService.getGroupBoardList(request);
        return ResponseEntity.ok(groupBoards);
    }

    @GetMapping("/{group_board_id}")
    public ResponseEntity<GroupBoardDto> getGroupBoard(@PathVariable("group_board_id") Long groupBoardId){
        GroupBoardDto groupBoardDto = groupBoardService.getGroupBoard(groupBoardId);
        return ResponseEntity.ok(groupBoardDto);
    }

    @PostMapping("/{group_board_id}/join")
    public ResponseEntity<Void> joinGroupBoard(
            @PathVariable("group_board_id") Long groupBoardId,
            HttpServletRequest request) {

        User currentUser = userService.getUser(request);
        groupBoardService.joinGroupBoard(currentUser.getId(), groupBoardId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<GroupBoardListDto>> getMyGroupBoard(HttpServletRequest request){
        List<GroupBoardListDto> groupBoards = groupBoardService.getMyGroupBoard(request);
        return ResponseEntity.ok(groupBoards);
    }

    @GetMapping("categories/{category_id}")
    public ResponseEntity<List<GroupBoardListDto>> getGroupBoardCategory(@PathVariable("category_id") Long categoryId){
        List<GroupBoardListDto> groupBoards = groupBoardService.getGroupBoardCategory(categoryId);
        return ResponseEntity.ok(groupBoards);
    }

    @GetMapping("/users/{user_id}")
    public ResponseEntity<List<GroupBoardListDto>> getUserGroupBoard(@PathVariable("user_id") Long userId){
        List<GroupBoardListDto> groupBoards = groupBoardService.getUserGroupBoard(userId);
        return ResponseEntity.ok(groupBoards);
    }
}
