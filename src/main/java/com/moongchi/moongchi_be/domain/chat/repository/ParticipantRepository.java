package com.moongchi.moongchi_be.domain.chat.repository;


import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant,Long> {

    int countByChatRoomId(Long chatRoomId);
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);
    Optional<Participant> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);
    List<Participant> findAllByChatRoomId(Long chatRoomId);
    List<Participant> findByUserId(Long userId);
    List<Participant> findByChatRoomId(Long chatRoomId);

}
