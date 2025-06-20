package com.moongchi.moongchi_be.domain.group_boards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GroupBoardRecommendDto {
    private String status;
    private RecommendationData data;

    @Data
    public static class RecommendationData {
        @JsonProperty("popular_groups")
        private List<PopularGroupDto> popularGroups;
    }

    @Data
    public static class PopularGroupDto {
        @JsonProperty("group_id")
        private Long groupId;
    }
}
