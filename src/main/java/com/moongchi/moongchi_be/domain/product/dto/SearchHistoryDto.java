package com.moongchi.moongchi_be.domain.product.dto;

import com.moongchi.moongchi_be.domain.product.entity.SearchHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SearchHistoryDto {
    private Long id;
    private String keyword;
    private LocalDateTime searchAt;

    public static SearchHistoryDto from(SearchHistory entity) {
        return new SearchHistoryDto(entity.getId(), entity.getKeyword(), entity.getSearchAt());
    }
}
