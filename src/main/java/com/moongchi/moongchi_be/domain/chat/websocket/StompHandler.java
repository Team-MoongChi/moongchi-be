package com.moongchi.moongchi_be.domain.chat.websocket;

import com.moongchi.moongchi_be.common.auth.jwt.JwtTokenProvider;
import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final WebSocketConnectionLimiter limiter;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String rawToken = accessor.getFirstNativeHeader("Authorization");
            if (rawToken == null || !rawToken.startsWith("Bearer ")) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            String token = rawToken.substring(7);
            if (!jwtTokenProvider.validateToken(token)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            Long userId = jwtTokenProvider.getUserId(token);
            String sessionId = accessor.getSessionId();

            String uri = accessor.getNativeHeader("origin") != null ? accessor.getNativeHeader("origin").toString() : accessor.getDestination();
            String chatRoomIdHeader = accessor.getFirstNativeHeader("chatRoomId");
            if (chatRoomIdHeader == null) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
            Long chatRoomId = Long.parseLong(chatRoomIdHeader);

            if (limiter.isExceedingLimit(userId, chatRoomId)) {
                throw new CustomException(ErrorCode.CONFLICT);
            }

            limiter.connect(userId, chatRoomId, sessionId);
            accessor.setUser(new StompPrincipal(String.valueOf(userId)));
//            log.info("🧠 [CONNECT] userId={}, chatRoomId={}, sessionId={}", userId, chatRoomId, sessionId);
        }

        return message;
    }
}
