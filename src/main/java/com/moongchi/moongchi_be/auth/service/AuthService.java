package com.moongchi.moongchi_be.auth.service;

import com.moongchi.moongchi_be.auth.dto.TokenResponseDto;
import com.moongchi.moongchi_be.auth.jwt.JwtTokenProvider;
import com.moongchi.moongchi_be.user.entity.User;
import com.moongchi.moongchi_be.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponseDto issueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshToken(request);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtTokenProvider.getUserPk(refreshToken);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();
        String accessToken = jwtTokenProvider.createToken(user.getEmail(), user.getName(), user.getUserRole());

        Cookie deleteAccessTokenCookie = new Cookie("access_token", null);
        deleteAccessTokenCookie.setPath("/");
        deleteAccessTokenCookie.setMaxAge(0);
        deleteAccessTokenCookie.setHttpOnly(true);
        deleteAccessTokenCookie.setSecure(true);
        response.addCookie(deleteAccessTokenCookie);

        return new TokenResponseDto(accessToken);
    }

    public TokenResponseDto reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshToken(request);

        String email = jwtTokenProvider.getUserPk(refreshToken);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();
        String newAccessToken = jwtTokenProvider.createToken(user.getEmail(), user.getName(), user.getUserRole());
        return new TokenResponseDto(newAccessToken);

    }


    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
