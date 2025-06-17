package com.moongchi.moongchi_be.domain.chat.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryConnectionLimiter implements WebSocketConnectionLimiter {

    private final Map<UserChatKey, String> connectionMap = new ConcurrentHashMap<>();
    private final Map<String, UserChatKey> sessionIdToKey = new ConcurrentHashMap<>();

    @Override
    public void connect(Long userId, Long chatRoomId, String sessionId) {
        UserChatKey key = new UserChatKey(userId, chatRoomId);
        connectionMap.put(key, sessionId);
        sessionIdToKey.put(sessionId, key);
    }

    @Override
    public void disconnect(Long userId, Long chatRoomId, String sessionId) {
        UserChatKey key = new UserChatKey(userId, chatRoomId);
        connectionMap.remove(key);
        sessionIdToKey.remove(sessionId);
    }

    @Override
    public boolean isExceedingLimit(Long userId, Long chatRoomId) {
        UserChatKey key = new UserChatKey(userId, chatRoomId);
        return connectionMap.containsKey(key);
    }

    @Override
    public ConnectionInfo getConnectionInfoBySessionId(String sessionId) {
        UserChatKey key = sessionIdToKey.get(sessionId);
        return key != null ? new ConnectionInfo(key.userId(), key.chatRoomId()) : null;
    }
}
