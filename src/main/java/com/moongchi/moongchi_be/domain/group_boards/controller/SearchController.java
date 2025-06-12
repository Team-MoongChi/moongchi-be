package com.moongchi.moongchi_be.domain.group_boards.controller;

import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.group_boards.service.SearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "공구상품", description = "공구상품 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group-boards")
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<List<GroupBoardListDto>> search(@RequestParam String keyword){
        List<GroupBoardListDto> groupBoardDtos = searchService.search(keyword);
        return ResponseEntity.ok(groupBoardDtos);
    }
}
