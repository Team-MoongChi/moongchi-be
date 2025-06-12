package com.moongchi.moongchi_be.domain.user.controller;

import com.moongchi.moongchi_be.common.util.CookieUtil;
import com.moongchi.moongchi_be.domain.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.domain.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> issueToken(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = CookieUtil.getCookieValue(request,"refresh_token");

        TokenResponseDto tokenResponseDto = authService.issueToken(refreshToken);

        CookieUtil.deleteCookie(response,"access_token");
        return ResponseEntity.ok(tokenResponseDto);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissueToken(HttpServletRequest request){
        String refreshToken = CookieUtil.getCookieValue(request, "refresh_token");
        TokenResponseDto tokenResponseDto = authService.reissueToken(refreshToken);
        return ResponseEntity.ok(tokenResponseDto);
    }

}
