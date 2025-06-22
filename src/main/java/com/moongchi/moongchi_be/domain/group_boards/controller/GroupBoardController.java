package com.moongchi.moongchi_be.domain.group_boards.controller;

import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardRequestDto;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.service.GroupBoardRecommendService;
import com.moongchi.moongchi_be.domain.group_boards.service.GroupBoardService;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "공동구매", description = "공동구매 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group-boards")
public class GroupBoardController {

    private final GroupBoardService groupBoardService;
    private final UserService userService;
    private final GroupBoardRecommendService groupBoardRecommendService;

    @Operation(summary = "공동구매 게시글 추가", description = "공동구매 게시글 업로드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시물 추가 성공")
    })
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody GroupBoardRequestDto dto, HttpServletRequest request) {
        User user = userService.getUser(request);
        groupBoardService.createPost(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시물이 추가되었습니다.");
    }

    @Operation(summary = "공동 구매 게시글 수정", description = "공동 구매 게시글 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공")
    })
    @PutMapping("/{groupBoardId}")
    public ResponseEntity<?> updatePost(@PathVariable Long groupBoardId, @RequestBody GroupBoardRequestDto dto) {
        groupBoardService.updatePost(groupBoardId, dto);
        return ResponseEntity.status(HttpStatus.OK).body("게시글이 수정되었습니다.");
    }

    @Operation(summary = "공동 구매 게시글 삭제", description = "공동 구매 게시글 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공")
    })
    @DeleteMapping("/{groupBoardId}")
    public ResponseEntity<?> deletePost(@PathVariable Long groupBoardId) {
        groupBoardService.deletePost(groupBoardId);
        return ResponseEntity.status(HttpStatus.OK).body("게시글이 삭제되었습니다.");
    }

    @Operation(summary = "공동 구매 게시글 목록 조회", description = "공동 구매 게시글 위치기반 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupBoardListDto.class)))
            )
    })
    @GetMapping
    public ResponseEntity<List<GroupBoardListDto>> getGroupBoardList(HttpServletRequest request) {
        User user = userService.getUser(request);
        List<GroupBoardListDto> groupBoards = groupBoardService.getGroupBoardList(user);
        return ResponseEntity.status(HttpStatus.OK).body(groupBoards);
    }

    @Operation(summary = "공동 구매 게시글 상세 조회", description = "공동 구매 게시글 상세 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = GroupBoardDto.class)))
    })
    @GetMapping("/{groupBoardId}")
    public ResponseEntity<GroupBoardDto> getGroupBoard(@PathVariable Long groupBoardId, HttpServletRequest request) {
        User user = userService.getUser(request);
        GroupBoardDto groupBoardDto = groupBoardService.getGroupBoard(groupBoardId, user);
        return ResponseEntity.status(HttpStatus.OK).body(groupBoardDto);
    }

    @Operation(summary = "공동 구매 참여", description = "공동 구매 참여")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참여 성공")
    })
    @PostMapping("/{groupBoardId}/join")
    public ResponseEntity<Void> joinGroupBoard(
            @PathVariable Long groupBoardId,
            HttpServletRequest request) {

        User currentUser = userService.getUser(request);
        groupBoardService.joinGroupBoard(currentUser.getId(), groupBoardId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "내가 올린 공동 구매 게시글 목록 조회", description = "내가 올린 공동 구매 게시글 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupBoardListDto.class))))
    })
    @GetMapping("/me")
    public ResponseEntity<List<GroupBoardListDto>> getMyGroupBoard(HttpServletRequest request) {
        User user = userService.getUser(request);
        List<GroupBoardListDto> groupBoards = groupBoardService.getMyGroupBoard(user);
        return ResponseEntity.status(HttpStatus.OK).body(groupBoards);
    }

    @Operation(summary = "카테고리 별 공동 구매 게시글 목록 조회", description = "카테고리 별 공동 구매 게시글 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupBoardListDto.class))))
    })
    @GetMapping("categories/{categoryId}")
    public ResponseEntity<List<GroupBoardListDto>> getGroupBoardCategory(@PathVariable Long categoryId, HttpServletRequest request) {
        User user = userService.getUser(request);
        List<GroupBoardListDto> groupBoards = groupBoardService.getGroupBoardCategory(categoryId, user);
        return ResponseEntity.status(HttpStatus.OK).body(groupBoards);
    }

    @Operation(summary = "사용자 별 공동 구매 게시글 목록 조회", description = "사용자 별 공동 구매 게시글 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupBoardListDto.class))))
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<GroupBoardListDto>> getUserGroupBoard(@PathVariable Long userId) {
        List<GroupBoardListDto> groupBoards = groupBoardService.getUserGroupBoard(userId);
        return ResponseEntity.status(HttpStatus.OK).body(groupBoards);
    }


    @Operation(summary = "공동 구매 게시글 수정 화면", description = "공동 구매 게시글 수정 시 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupBoardDto.class))))
    })
    @GetMapping("/{groupBoardId}/edit")
    public ResponseEntity<GroupBoardDto> getEditGroupBoard(@PathVariable Long groupBoardId) {
        GroupBoardDto groupBoardDto = groupBoardService.getEditGroupBoard(groupBoardId);
        return ResponseEntity.status(HttpStatus.OK).body(groupBoardDto);
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<GroupBoardListDto>> getReccomendGroupBoard(HttpServletRequest request) {
        User user = userService.getUser(request);
//        List<GroupBoardListDto> groupBoardListDtos = groupBoardRecommendService.getRecommendGroupBoard(user.getId());
        List<GroupBoardListDto> groupBoardListDtos = groupBoardRecommendService.getRecommendGroupBoard(2L);

        return ResponseEntity.status(HttpStatus.OK).body(groupBoardListDtos);
    }


}
