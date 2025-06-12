package com.moongchi.moongchi_be.domain.user.controller;

import com.moongchi.moongchi_be.domain.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> issueToken(HttpServletRequest request, HttpServletResponse response){
        TokenResponseDto tokenResponseDto = authService.issueToken(request,response);
        return ResponseEntity.ok(tokenResponseDto);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissueToken(HttpServletRequest request, HttpServletResponse response){
        TokenResponseDto tokenResponseDto = authService.reissueToken(request, response);
        return ResponseEntity.ok(tokenResponseDto);
    }

}
