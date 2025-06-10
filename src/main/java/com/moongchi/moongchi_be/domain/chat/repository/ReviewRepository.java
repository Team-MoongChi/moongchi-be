package com.moongchi.moongchi_be.domain.chat.repository;

import com.moongchi.moongchi_be.domain.chat.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    @Query("SELECT COUNT(r) > 0 FROM Review r " +
            "JOIN r.participant p " +
            "WHERE r.groupBoard.id = :groupBoardId " +
            "AND r.participant.id = :targetParticipantId " +
            "AND p.groupBoard.id = :groupBoardId " +
            "AND EXISTS (SELECT 1 FROM Participant w " +
            "WHERE w.groupBoard.id = :groupBoardId " +
            "AND w.user.id = :writerUserId)")
    boolean existsByTargetAndWriter(
            @Param("groupBoardId") Long groupBoardId,
            @Param("targetParticipantId") Long targetParticipantId,
            @Param("writerUserId") Long writerUserId
    );

    @Query("SELECT r.participant.id FROM Review r " +
            "JOIN r.participant p " +
            "WHERE r.groupBoard.id = :groupBoardId " +
            "AND EXISTS (SELECT 1 FROM Participant w " +
            "WHERE w.groupBoard.id = :groupBoardId " +
            "AND w.user.id = :writerUserId)")
    List<Long> findTargetsByWriter(
            @Param("groupBoardId") Long groupBoardId,
            @Param("writerUserId") Long writerUserId
    );
}
