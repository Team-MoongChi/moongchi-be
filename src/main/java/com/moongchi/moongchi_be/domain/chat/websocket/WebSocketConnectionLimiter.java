package com.moongchi.moongchi_be.domain.chat.websocket;

public interface WebSocketConnectionLimiter {
    void connect(Long userId, Long chatRoomId, String sessionId);
    void disconnect(Long userId, Long chatRoomId, String sessionId);
    boolean isExceedingLimit(Long userId, Long chatRoomId);
    ConnectionInfo getConnectionInfoBySessionId(String sessionId);

    record ConnectionInfo(Long userId, Long chatRoomId) {}
}
