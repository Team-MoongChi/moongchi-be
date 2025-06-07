package com.moongchi.moongchi_be.domain.chat.entity;

import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column(name = "title", length = 20, nullable = false)
    private String title;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private ChatRoomStatus status = ChatRoomStatus.RECRUITING;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "send_at")
    private LocalDateTime sendAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_board_id")
    private GroupBoard groupBoard;

}
