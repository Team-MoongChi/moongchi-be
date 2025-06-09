package com.moongchi.moongchi_be.domain.group_boards.dto;

import com.moongchi.moongchi_be.domain.chat.dto.BoardParticipantDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBoardDto {
    private Long id;
    private String title;
    private String content;
    private String location;
    private String boardStatus;
    private LocalDate deadline;
    private int totalUsers;
    private int currentUsers;
    private GroupProductDto groupProduct;
    private List<BoardParticipantDto> participants;
}


