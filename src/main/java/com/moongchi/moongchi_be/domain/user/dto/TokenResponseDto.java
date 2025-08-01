package com.moongchi.moongchi_be.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "토큰 응답 DTO")
public class TokenResponseDto {
    private String accessToken;
}

