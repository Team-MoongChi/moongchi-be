package com.moongchi.moongchi_be.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private Double star;
    private List<String> keywords;
    private String  review;
    private Long participantId;
    private Long groupBoardId;
    private LocalDateTime createdAt;
}
