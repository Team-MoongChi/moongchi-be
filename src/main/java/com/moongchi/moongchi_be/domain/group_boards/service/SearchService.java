package com.moongchi.moongchi_be.domain.group_boards.service;

import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardDto;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final GroupBoardRepository groupBoardRepository;
    private final GroupBoardService groupBoardService;

    public List<GroupBoardDto> search(String keyword){
        List<GroupBoard> groupBoards = groupBoardRepository.findByTitleContaining(keyword);

        return groupBoards.stream()
                .map(groupBoardService::convertToDto)
                .collect(Collectors.toList());
    }
}
