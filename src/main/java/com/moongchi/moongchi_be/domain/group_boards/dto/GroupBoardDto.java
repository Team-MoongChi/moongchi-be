package com.moongchi.moongchi_be.domain.group_boards.dto;

import com.moongchi.moongchi_be.domain.chat.dto.BoardParticipantDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class GroupBoardDto {
    private Long id;
    private String title;
    private Integer price;
    private String content;
    private String location;
    private String boardStatus;
    private LocalDate deadline;
    private Integer totalUser;
    private Integer currentUsers;
    private String productUrl;
    private int likeCount;
    private boolean editable;
    private Long chatRoomId;
    private List<String> images;
    private List<BoardParticipantDto> participants;
}


