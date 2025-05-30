package com.moongchi.moongchi_be.domain.group_boards.dto;

import com.moongchi.moongchi_be.domain.group_boards.entity.GroupProduct;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBoardDto {
    private Long id;
    private String title;
    private String content;
    private String location;
    private BoardStatus boardStatus;
    private LocalDate deadline;
    private int totalUsers;
    private LocalDateTime createAt;
    private GroupProduct groupProduct;
}


