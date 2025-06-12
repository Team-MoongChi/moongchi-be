package com.moongchi.moongchi_be.domain.user.controller;

import com.moongchi.moongchi_be.common.util.CookieUtil;
import com.moongchi.moongchi_be.domain.user.dto.LogoutResponseDto;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.LogoutService;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저", description = "유저 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/auth/logout")
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;
    private final UserService userService;

    @Operation(summary = "소셜 로그아웃", description="로그아웃 후 리다이렉트 URL 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LogoutResponseDto.class))))
    })
    @PostMapping
    public ResponseEntity<LogoutResponseDto> logout(HttpServletRequest request, HttpServletResponse response) {
        User user = userService.getUser(request);
        CookieUtil.deleteCookie(response, "refresh_token");
        LogoutResponseDto logoutResponseDto = logoutService.logout(user);
        return ResponseEntity.ok(logoutResponseDto);
    }
}
