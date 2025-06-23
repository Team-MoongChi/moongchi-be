package com.moongchi.moongchi_be.common.auth.oauth2;

import com.moongchi.moongchi_be.common.auth.jwt.JwtTokenProvider;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.enums.UserRole;
import com.moongchi.moongchi_be.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    private static final String CALLBACK_URI = "https://moongchi-phi.vercel.app/oauth/callback";
    private static final String SIGNUP_URI = "https://moongchi-phi.vercel.app/signup";


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuthAttributes userInfo = OAuthAttributes.of(provider, attributes);

        Optional<User> existingUserOpt = userRepository.findByProviderAndEmail(userInfo.getProvider(), userInfo.getEmail());
        boolean isNewUser = existingUserOpt.isEmpty();

        User user = isNewUser ? registerNewUser(userInfo) : updateExistingUser(existingUserOpt.get(), userInfo);

        // 리프레쉬 토큰 발급
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        addCookie(response, "refresh_token", refreshToken, 7 * 24 * 60 * 60); // 7일 유지

        String targetUrl;

        if (isNewUser) {
            //신규유저
            targetUrl = SIGNUP_URI;
        } else {
            //기존유저
            String accessToken = jwtTokenProvider.createToken(user.getId(), user.getUserRole());
            addCookie(response, "access_token", accessToken, 60 * 60); // 1시간 유지

            targetUrl = CALLBACK_URI;
        }

        log.info("[OAuth2 로그인 성공] 리다이렉트 URL: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private User registerNewUser(OAuthAttributes attributes) {
        try {
            User newUser = User.builder()
                    .name(attributes.getName())
                    .email(attributes.getEmail())
                    .provider(attributes.getProvider())
                    .userRole(UserRole.USER)
                    .build();
            return userRepository.save(newUser);
        } catch (Exception e) {
            log.error("신규 유저 등록 실패", e);
            throw new RuntimeException("유저 등록 중 오류가 발생했습니다.");
        }
    }

    private User updateExistingUser(User existingUser, OAuthAttributes attributes) {
        existingUser.update(attributes.getName());
        return userRepository.save(existingUser);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSeconds);
        response.addCookie(cookie);
    }
}
