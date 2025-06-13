package com.moongchi.moongchi_be.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 이름, 이메일 응답 DTO")
public class UserBasicDto {
    private String name;
    private String email;
}
