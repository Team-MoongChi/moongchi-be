package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.domain.chat.entity.*;
import com.moongchi.moongchi_be.domain.chat.repository.ChatRoomRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @Transactional
    public void joinChatRoom(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        GroupBoard groupBoard = chatRoom.getGroupBoard();

        // 이미 참여한 유저인지 체크
        boolean alreadyJoined = participantRepository.existsByChatRoomIdAndUserId(chatRoomId, userId);
        if (alreadyJoined) {
            throw new IllegalStateException("이미 참여한 채팅방입니다.");
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Participant participant = new Participant();
        participant.setChatRoom(chatRoom);
        participant.setGroupBoard(groupBoard);
        participant.setUser(user);
        participant.setRole(Role.MEMBER);  // 일반 참여자 역할
        participant.setJoinedAt(LocalDateTime.now());
        participant.setPaymentStatus(PaymentStatus.UNPAID);

        participantRepository.save(participant);

        int currentParticipants = participantRepository.countByChatRoomId(chatRoomId);
        int maxCount = groupBoard.getTotalUsers();

        if (currentParticipants ==  maxCount) {

            chatRoom.setStatus(ChatRoomStatus.PAYING);
            groupBoard.setBoardStatus(BoardStatus.CLOSED);
            chatRoomRepository.save(chatRoom);  // 상태 저장
        }
    }

    @Transactional
    public void pay(Long chatRoomId, Long userId) {
        Participant participant = participantRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        if (participant.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("이미 결제한 사용자입니다.");
        }

        // 결제 상태 변경
        participant.setPaymentStatus(PaymentStatus.PAID);
        participantRepository.save(participant);

        // 모든 참가자가 결제했는지 확인
        List<Participant> participants = participantRepository.findAllByChatRoomId(chatRoomId);

        boolean allPaid = participants.stream()
                .allMatch(p -> p.getPaymentStatus() == PaymentStatus.PAID);

        if (allPaid) {
            // 채팅방 상태 직접 변경 (메시지 발송 없이)
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
            chatRoom.setStatus(ChatRoomStatus.PURCHASED);
            chatRoomRepository.save(chatRoom);
            }
        }

    @Transactional
    public void completeTrade(Long chatRoomId, Long userId) {
        Participant participant = participantRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("참여자 정보를 찾을 수 없습니다."));
        GroupBoard groupBoard = participant.getGroupBoard();

        if (participant.getRole() == Role.LEADER) {
            throw new IllegalStateException("리더는 거래완료 버튼을 누를 수 없습니다.");
        }

        if (participant.isTradeCompleted()) {
            throw new IllegalStateException("이미 거래완료 처리된 사용자입니다.");
        }

        participant.setTradeCompleted(true);
        participantRepository.save(participant);

        // 리더 제외 모든 멤버가 거래완료 눌렀는지 확인
        List<Participant> participants = participantRepository.findAllByChatRoomId(chatRoomId);

        boolean allCompleted = participants.stream()
                .filter(p -> p.getRole() != Role.LEADER)
                .allMatch(Participant::isTradeCompleted);

        if (allCompleted) {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
            chatRoom.setStatus(ChatRoomStatus.COMPLETED);
            groupBoard.setBoardStatus(BoardStatus.COMPLETED);

            chatRoomRepository.save(chatRoom);
        }
    }

}
