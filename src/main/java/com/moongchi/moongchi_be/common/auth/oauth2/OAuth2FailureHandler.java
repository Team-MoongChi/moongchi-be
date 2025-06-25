package com.moongchi.moongchi_be.common.auth.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2FailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        log.error("OAuth2 로그인 실패: {}", exception.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String errorCode = "UNKNOWN";
        String errorDescription = "No description";

        if (exception instanceof OAuth2AuthenticationException authException) {
            OAuth2Error error = authException.getError();
            errorCode = error.getErrorCode();
            errorDescription = error.getDescription();

            log.error("OAuth2 에러 코드: {}", errorCode);
            log.error("OAuth2 에러 설명: {}", errorDescription);
            log.error("OAuth2 에러 URI: {}", error.getUri());
        }

        String json = String.format(
                "{\"error\": \"%s\", \"description\": \"%s\"}",
                errorCode, errorDescription != null ? errorDescription : "No description"
        );

        response.getWriter().write(json);
    }
}

