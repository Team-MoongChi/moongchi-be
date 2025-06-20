package com.moongchi.moongchi_be.domain.group_boards.service;

import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import com.moongchi.moongchi_be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final GroupBoardRepository groupBoardRepository;
    private final GroupBoardService groupBoardService;

    public List<GroupBoardListDto> search(String keyword, User user){

        List<GroupBoard> groupBoards = groupBoardRepository.findSearchLocationNear(keyword, user.getLatitude(), user.getLongitude());

        return groupBoards.stream()
                .map(groupBoardService::convertToListDto)
                .collect(Collectors.toList());
    }
}
