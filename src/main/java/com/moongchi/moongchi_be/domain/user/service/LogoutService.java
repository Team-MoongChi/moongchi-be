package com.moongchi.moongchi_be.domain.user.service;

import com.moongchi.moongchi_be.domain.user.entity.User;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final UserService userService;

    public Map<String, String> logout(HttpServletRequest request, HttpServletResponse response){

        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        User user = userService.getUser(request);
        String provider = user.getProvider();
        String redirectUrl = socialLogoutRedirectUrl(provider);

        Map<String, String> map = new HashMap<>();
        map.put("redirectUrl", redirectUrl);

        return map;
    }

    private String socialLogoutRedirectUrl(String provider){
        switch (provider){
            case "kakao":
                Dotenv dotenv = Dotenv.load();
                String kakaoClientId = dotenv.get("KAKAO_CLIENT_ID");
                String kakaoRedirectUri = dotenv.get("KAKAP_REDIRECT_URI");
                return "https://kauth.kakao.com/oauth/logout?client_id=" + kakaoClientId + "&logout_redirect_uri="
                        + kakaoRedirectUri;
            case "google":
                return "https://accounts.google.com/Logout";
            case "naver":
            default:
                return "";
        }
    }
}
