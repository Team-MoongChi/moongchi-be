package com.moongchi.moongchi_be.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class ReviewKeywordDto {
    private Long userId;
    private List<String> keywords;
}
