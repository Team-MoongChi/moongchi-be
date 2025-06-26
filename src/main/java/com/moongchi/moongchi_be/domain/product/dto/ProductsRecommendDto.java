package com.moongchi.moongchi_be.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ProductsRecommendDto {
    private String status;
    private MlopsData data;
    private String timestamp;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class MlopsData {
        @JsonProperty("doc_id")
        private String docId;

        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("recommended_item_ids")
        private List<String> recommendedItemIds;

        @JsonProperty("experiment_id")
        private String experimentId;

        @JsonProperty("run_id")
        private String runId;
    }
}
