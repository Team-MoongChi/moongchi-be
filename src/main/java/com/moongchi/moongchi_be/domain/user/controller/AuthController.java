package com.moongchi.moongchi_be.domain.user.controller;

import com.moongchi.moongchi_be.common.util.CookieUtil;
import com.moongchi.moongchi_be.domain.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.domain.user.service.AuthService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저", description = "유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "토큰 발급", description = "로그인 성공시 토큰 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발급 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TokenResponseDto.class))))
    })
    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> issueToken(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = CookieUtil.getCookieValue(request,"refresh_token");

        TokenResponseDto tokenResponseDto = authService.issueToken(refreshToken);

        CookieUtil.deleteCookie(response,"access_token");
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponseDto);
    }

    @Operation(summary = "토큰 재발급", description = "토큰 만료 시 토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재발급 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TokenResponseDto.class))))
    })
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissueToken(HttpServletRequest request){
        String refreshToken = CookieUtil.getCookieValue(request, "refresh_token");
        TokenResponseDto tokenResponseDto = authService.reissueToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponseDto);
    }

}
