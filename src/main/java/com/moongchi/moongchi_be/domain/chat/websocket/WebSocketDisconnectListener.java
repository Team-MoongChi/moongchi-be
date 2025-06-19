package com.moongchi.moongchi_be.domain.chat.websocket;

import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketDisconnectListener {

    private final WebSocketConnectionLimiter limiter;
    private final ParticipantRepository participantRepository;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        WebSocketConnectionLimiter.ConnectionInfo info = limiter.getConnectionInfoBySessionId(sessionId);

        if (info != null) {
            participantRepository.findByChatRoomIdAndUserId(info.chatRoomId(), info.userId())
                    .ifPresentOrElse(participant -> {
                        participant.setReadAt(LocalDateTime.now());
                        participantRepository.save(participant);
//                        log.info("✅ 읽음 처리 완료: userId={}, chatRoomId={}", info.userId(), info.chatRoomId());
                    }, () -> {
//                        log.warn("❗ 참가자 없음: userId={}, chatRoomId={}", info.userId(), info.chatRoomId());
                    });

            limiter.disconnect(info.userId(), info.chatRoomId(), sessionId);
        }
    }

}
