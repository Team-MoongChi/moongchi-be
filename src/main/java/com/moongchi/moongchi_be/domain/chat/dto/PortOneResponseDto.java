package com.moongchi.moongchi_be.domain.chat.dto;

import lombok.Getter;

@Getter
public class PortOneResponseDto {
    private int code;
    private String message;
    private AccessTokenResponse response;

    @Getter
    public static class AccessTokenResponse {
        private String access_token;
        private long now;
        private long expired_at;
    }
}
