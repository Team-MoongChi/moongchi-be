package com.moongchi.moongchi_be.auth.controller;

import com.moongchi.moongchi_be.auth.dto.TokenResponseDto;
import com.moongchi.moongchi_be.auth.service.AuthService;
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
        TokenResponseDto tokenResponseDto = authService.issueToken(request,response);
        return ResponseEntity.ok(tokenResponseDto);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissueToken(HttpServletRequest request, HttpServletResponse response){
        TokenResponseDto tokenResponseDto = authService.reissueToken(request, response);
        return ResponseEntity.ok(tokenResponseDto);
    }

}
