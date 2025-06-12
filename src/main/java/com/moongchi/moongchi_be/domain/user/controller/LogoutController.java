package com.moongchi.moongchi_be.domain.user.controller;

import com.moongchi.moongchi_be.common.util.CookieUtil;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.LogoutService;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.moongchi.moongchi_be.domain.user.service.UserService;
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
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        User user = userService.getUser(request);
        CookieUtil.deleteCookie(response, "refresh_token");
        Map<String, String> map = logoutService.logout(user);
        return ResponseEntity.ok(map);
    }
}
