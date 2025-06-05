package com.moongchi.moongchi_be.domain.group_boards.dto;

import com.moongchi.moongchi_be.domain.chat.dto.ParticipantDto;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBoardListDto {
    private Long id;
    private int price;
    private String location;
    private BoardStatus boardStatus;
    private int totalUsers;
    private int currentUsers;
    private String image;
    private List<ParticipantDto> participants;
}
