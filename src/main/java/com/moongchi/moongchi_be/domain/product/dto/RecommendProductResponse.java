package com.moongchi.moongchi_be.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendProductResponse {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("recommended_item_ids")
    private List<Long> recommendedItemIds;

    @JsonProperty("experiment_id")
    private Long experimentId;

    @JsonProperty("run_id")
    private String runId;

}
