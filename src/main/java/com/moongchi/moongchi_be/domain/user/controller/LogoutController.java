package com.moongchi.moongchi_be.domain.user.controller;

import com.moongchi.moongchi_be.domain.user.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
