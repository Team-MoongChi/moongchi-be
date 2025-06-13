package com.moongchi.moongchi_be.domain.group_boards.controller;

import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.group_boards.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "공동구매", description = "공동구매 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group-boards")
public class SearchController {
    private final SearchService searchService;

    @Operation(summary = "공동구매 게시글 검색", description = "공동구매 게시글 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupBoardListDto.class))))
    })
    @GetMapping("/search")
    public ResponseEntity<List<GroupBoardListDto>> search(@RequestParam String keyword){
        List<GroupBoardListDto> groupBoardDtos = searchService.search(keyword);
        return ResponseEntity.ok(groupBoardDtos);
    }
}
