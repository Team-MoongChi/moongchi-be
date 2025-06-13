package com.moongchi.moongchi_be.domain.user.service;

import com.moongchi.moongchi_be.domain.user.dto.LogoutResponseDto;
import com.moongchi.moongchi_be.domain.user.entity.User;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final UserService userService;

    public LogoutResponseDto logout(User user){
        String provider = user.getProvider();
        String redirectUrl = socialLogoutRedirectUrl(provider);

        return new LogoutResponseDto(redirectUrl);
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
