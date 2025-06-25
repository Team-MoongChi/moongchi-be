package com.moongchi.moongchi_be.domain.chat.repository;

import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findByUserId(Long userId);

    @Query("SELECT p FROM Participant p WHERE p.groupBoard.chatRoom.id = :chatRoomId")
    List<Participant> findAllByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT COUNT(p) FROM Participant p JOIN p.groupBoard gb JOIN gb.chatRoom cr WHERE cr.id = :chatRoomId")
    int countByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT COUNT(p) FROM Participant p WHERE p.groupBoard.id = :groupBoardId")
    int countByGroupBoardId(@Param("groupBoardId") Long groupBoardId);

    boolean existsByUserIdAndGroupBoardId(Long userId, Long groupBoardId);

    @Query("SELECT p FROM Participant p WHERE p.groupBoard.chatRoom.id = :chatRoomId AND p.user.id = :userId")
    Optional<Participant> findByChatRoomIdAndUserId(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    @Query("""
    SELECT p FROM Participant p 
    JOIN FETCH p.groupBoard gb 
    JOIN FETCH gb.chatRoom 
    WHERE gb.chatRoom.id = :chatRoomId AND p.user.id = :userId
    """)
    Optional<Participant> findWithChatRoomByChatRoomIdAndUserId(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId
    );
}
