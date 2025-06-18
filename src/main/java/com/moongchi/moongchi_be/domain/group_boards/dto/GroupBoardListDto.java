package com.moongchi.moongchi_be.domain.group_boards.dto;

import com.moongchi.moongchi_be.domain.chat.dto.BoardParticipantDto;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
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
@Schema(description = "공동구매 게시글 목록 응답 DTO")
public class GroupBoardListDto {
    private Long id;
    private String title;
    private Integer price;
    private String location;
    private double latitude;
    private double longitude;
    private Long largeCategoryId;
    private BoardStatus boardStatus;
    private Integer totalUsers;
    private Integer currentUsers;
    private String image;
    private LocalDateTime createAt;
    private List<BoardParticipantDto> participants;
}
