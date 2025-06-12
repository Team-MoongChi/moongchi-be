package com.moongchi.moongchi_be.domain.user.controller;

import com.moongchi.moongchi_be.domain.user.service.LogoutService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "유저", description = "유저 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/auth/logout")
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response){
        Map<String, String> map = logoutService.logout(request,response);
        return ResponseEntity.ok(map);
    }
}
