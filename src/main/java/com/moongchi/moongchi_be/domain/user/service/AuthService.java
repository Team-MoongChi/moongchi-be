package com.moongchi.moongchi_be.domain.user.service;

import com.moongchi.moongchi_be.common.auth.jwt.JwtTokenProvider;
import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponseDto issueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshToken(request);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getUserRole());

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

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        String newAccessToken = jwtTokenProvider.createToken(user.getId(), user.getUserRole());
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
