package com.moongchi.moongchi_be.domain.chat.websocket;

import com.moongchi.moongchi_be.common.auth.jwt.JwtTokenProvider;
import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String rawToken = accessor.getFirstNativeHeader("Authorization");
            if (rawToken == null) rawToken = accessor.getFirstNativeHeader("authorization");

            if (rawToken == null || !rawToken.startsWith("Bearer ")) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);            }

            String token = rawToken.substring(7);
            if (!jwtTokenProvider.validateToken(token)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);            }

            Long userId = jwtTokenProvider.getUserId(token);
            accessor.setUser(new StompPrincipal(String.valueOf(userId)));
        }

        return message;
    }

}
