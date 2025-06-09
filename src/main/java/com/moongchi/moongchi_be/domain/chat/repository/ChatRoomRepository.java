package com.moongchi.moongchi_be.domain.chat.repository;

import com.moongchi.moongchi_be.domain.chat.entity.ChatRoom;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    Optional<ChatRoom> findByGroupBoard(GroupBoard groupBoard);

}
