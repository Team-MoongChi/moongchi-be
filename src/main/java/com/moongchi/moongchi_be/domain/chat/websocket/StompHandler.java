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
                log.warn("❌ WebSocket STOMP 인증 실패: Authorization 헤더가 없거나 형식이 잘못됨.");
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            String token = rawToken.substring(7);
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("❌ WebSocket STOMP 인증 실패: 유효하지 않은 JWT 토큰.");
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            Long userId = jwtTokenProvider.getUserId(token);
            String sessionId = accessor.getSessionId();

            String chatRoomIdHeader = accessor.getFirstNativeHeader("chatRoomId");
            if (chatRoomIdHeader == null) {
                log.warn("❌ WebSocket STOMP 연결 실패: chatRoomId 헤더가 누락됨. userId={}", userId);
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
            Long chatRoomId = Long.parseLong(chatRoomIdHeader);

            if (limiter.isExceedingLimit(userId, chatRoomId)) {
                log.warn("❌ WebSocket STOMP 연결 제한 초과: userId={}, chatRoomId={}", userId, chatRoomId);
                throw new CustomException(ErrorCode.CONFLICT);
            }

            limiter.connect(userId, chatRoomId, sessionId);
            accessor.setUser(new StompPrincipal(String.valueOf(userId)));

            log.info("✅ [STOMP CONNECT SUCCESS] userId={}, chatRoomId={}, sessionId={}", userId, chatRoomId, sessionId);
        } else if (accessor != null && StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            if (sessionId != null) {
                log.info("⚡️ [STOMP DISCONNECT] sessionId={}", sessionId);
            }
        } else if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            String destination = accessor.getDestination();
            log.info("🔥 [STOMP SUBSCRIBE] sessionId={}, destination={}", sessionId, destination);
        } else if (accessor != null && StompCommand.SEND.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            String destination = accessor.getDestination();
            log.debug("➡️ [STOMP SEND] sessionId={}, destination={}", sessionId, destination);
        }

        return message;
    }
}