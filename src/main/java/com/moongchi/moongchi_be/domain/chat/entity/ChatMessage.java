package com.moongchi.moongchi_be.domain.chat.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter @Setter
@Document(collection = "chat_messages")
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {

    @Id
    private String id;

    private String chatRoomId;

    private String participantId;

    private MessageType messageType = MessageType.TEXT;

    @CreatedDate
    private LocalDateTime sendAt;

    private String message;

}
