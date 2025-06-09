package com.moongchi.moongchi_be.domain.chat.repository;

import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // 특정 유저가 참여한 모든 참가 정보
    List<Participant> findByUserId(Long userId);

    // 특정 채팅방에 속한 참여자 전체 조회
    @Query("SELECT p FROM Participant p WHERE p.groupBoard.chatRoom.id = :chatRoomId")
    List<Participant> findAllByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // 채팅방 ID 기준 인원 수
    @Query("SELECT COUNT(p) FROM Participant p JOIN p.groupBoard gb JOIN gb.chatRoom cr WHERE cr.id = :chatRoomId")
    int countByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // 공구글 ID 기준 인원 수
    @Query("SELECT COUNT(p) FROM Participant p WHERE p.groupBoard.id = :groupBoardId")
    int countByGroupBoardId(@Param("groupBoardId") Long groupBoardId);

    // 이미 참여했는지 확인 (중복 방지용)
    boolean existsByUserIdAndGroupBoardId(Long userId, Long groupBoardId);

}
