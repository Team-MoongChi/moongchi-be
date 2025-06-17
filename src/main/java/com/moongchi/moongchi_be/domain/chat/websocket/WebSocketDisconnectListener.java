package com.moongchi.moongchi_be.domain.chat.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketDisconnectListener {

    private final WebSocketConnectionLimiter limiter;

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        WebSocketConnectionLimiter.ConnectionInfo info = limiter.getConnectionInfoBySessionId(sessionId);
        if (info != null) {
            limiter.disconnect(info.userId(), info.chatRoomId(), sessionId);
//            log.info("🧹 [DISCONNECT] userId={}, chatRoomId={}, sessionId={}", info.userId(), info.chatRoomId(), sessionId);
        }
    }
}
