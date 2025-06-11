package com.moongchi.moongchi_be.domain.chat.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ReviewRequestDto {
    @NotNull
    @DecimalMin("0.5")
    @DecimalMax("5.0")
    private Double star;

    @NotBlank
    private List<String> keyword;

    private String review;
}
