package com.moongchi.moongchi_be.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MlopsRecommendResponse {
    private String status;
    private RecommendProductResponse data;
    private String timestamp;
}
