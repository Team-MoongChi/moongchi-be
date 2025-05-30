package com.moongchi.moongchi_be.domain.group_boards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "공동구매 request Dto")
public class GroupBoardRequestDto {

    @Schema(name = "이름")
    private String name;

    private int totalUsers;
    private String quantity;
    private int price;
    private String location;
    private String content;
    private LocalDate deadLine;
    private Long categoryId;
    private Long productId;
    private List<String> images;
}