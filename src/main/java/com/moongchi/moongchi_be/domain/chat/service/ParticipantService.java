package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.domain.chat.dto.ParticipantPaymentDto;
import com.moongchi.moongchi_be.domain.chat.entity.*;
import com.moongchi.moongchi_be.domain.chat.repository.ChatRoomRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.repository.UserRepository;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final GroupBoardRepository groupBoardRepository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    public List<ParticipantPaymentDto> getPaymentInfoByChatRoom(Long chatRoomId) {
        List<Participant> participants = participantRepository.findAllByChatRoomId(chatRoomId);
        GroupBoard groupBoard = groupBoardRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방 그룹게시판 없음"));

        int totalAmount = groupBoard.getGroupProduct().getPrice(); // 총 공구 금액
        int participantCount = participants.size();
        int perPersonAmount = participantCount == 0 ? 0 : totalAmount / participantCount;

        return participants.stream()
                .map(p -> new ParticipantPaymentDto(
                        p.getUser().getId(),
                        p.getUser().getName(),
                        p.getPaymentStatus(),
                        perPersonAmount
                )).collect(Collectors.toList());
    }

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


    //시뮬레이터를 위한 코드
    @Transactional
    public void simulateJoin(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        GroupBoard groupBoard = chatRoom.getGroupBoard();

        int maxCount = groupBoard.getTotalUsers();
        int currentCount = participantRepository.countByChatRoomId(chatRoomId);

        int toCreate = maxCount - currentCount;
        if (toCreate <= 0) {
            throw new IllegalStateException("참여자가 이미 최대 인원에 도달했습니다.");
        }
        for (int i = 1; i < maxCount; i++) {
            User fakeUser = new User().setName("시뮬레이터유저" + i);
            fakeUser = userRepository.save(fakeUser);

            Participant participant = new Participant();
            participant.setChatRoom(chatRoom);
            participant.setGroupBoard(groupBoard);
            participant.setUser(fakeUser);
            participant.setRole(Role.MEMBER);
            participant.setJoinedAt(LocalDateTime.now());
            participant.setPaymentStatus(PaymentStatus.UNPAID);
            participant.setSimulated(true);

            participantRepository.save(participant);
        }
    }
    @Transactional
    public void simulatePayment(Long chatRoomId) {
        Random random = new Random();
        List<Participant> participants = participantRepository.findAllByChatRoomId(chatRoomId);
        for (Participant p : participants) {
            if (random.nextDouble() < 0.2) {
                p.setPaymentStatus(PaymentStatus.UNPAID);
            } else {
                p.setPaymentStatus(PaymentStatus.PAID);
            }
            participantRepository.save(p);
        }
    }
    @Transactional
    public void simulatePurchase(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        chatRoom.setStatus(ChatRoomStatus.PURCHASED);
    }
    @Transactional
    public void simulateTradeComplete(Long chatRoomId) {
        Random random = new Random();
        List<Participant> participants = participantRepository.findAllByChatRoomId(chatRoomId);

        for (Participant p : participants) {
            if (p.getRole() != Role.LEADER) {
                // 70% 확률로 거래완료 처리, 30%는 미처리
                p.setTradeCompleted(random.nextDouble() < 0.7);
                participantRepository.save(p);
            }
        }

        // 리더 제외 모든 멤버가 거래완료 눌렀는지 확인
        boolean allCompleted = participants.stream()
                .filter(p -> p.getRole() != Role.LEADER)
                .allMatch(Participant::isTradeCompleted);

        if (allCompleted) {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
            GroupBoard groupBoard = chatRoom.getGroupBoard();

            chatRoom.setStatus(ChatRoomStatus.COMPLETED);
            groupBoard.setBoardStatus(BoardStatus.COMPLETED);

            chatRoomRepository.save(chatRoom);
        }
    }

}
