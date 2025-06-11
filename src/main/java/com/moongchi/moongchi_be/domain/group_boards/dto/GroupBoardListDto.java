package com.moongchi.moongchi_be.domain.group_boards.dto;

import com.moongchi.moongchi_be.domain.chat.dto.BoardParticipantDto;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBoardListDto {
    private Long id;
    private String title;
    private Integer price;
    private String location;
    private BoardStatus boardStatus;
    private Integer totalUsers;
    private Integer currentUsers;
    private String image;
    private LocalDateTime createAt;
    private List<BoardParticipantDto> participants;
}
