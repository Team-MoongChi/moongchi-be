package com.moongchi.moongchi_be.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "소셜 로그아웃 후 리다이렉트될 URL")
public class LogoutResponseDto {
    private String redirectUrl;
}
