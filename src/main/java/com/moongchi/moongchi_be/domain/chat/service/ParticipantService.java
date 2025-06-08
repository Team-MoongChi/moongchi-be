package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoom;
import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import com.moongchi.moongchi_be.domain.chat.entity.PaymentStatus;
import com.moongchi.moongchi_be.domain.chat.entity.Role;
import com.moongchi.moongchi_be.domain.chat.repository.ChatRoomRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import com.moongchi.moongchi_be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final GroupBoardRepository groupBoardRepository;
    private final ChatRoomService chatRoomService;

    public void joinGroupBoard(Long userId, Long groupBoardId) {
        GroupBoard board = groupBoardRepository.findById(groupBoardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (participantRepository.existsByUserIdAndGroupBoardId(userId, groupBoardId)) {
            throw new CustomException(ErrorCode.CONFLICT);
        }

        int currentCount = participantRepository.countByGroupBoardId(groupBoardId);
        if (currentCount >= board.getTotalUsers()) {
            throw new CustomException(ErrorCode.CONFLICT);
        }

        Participant participant = new Participant();
        participant.setUser(userRepository.findById(userId).orElseThrow());
        participant.setGroupBoard(board);
        participant.setPaymentStatus(PaymentStatus.UNPAID);
        participant.setTradeCompleted(false);
        participant.setRole(Role.MEMBER);
        participant.setJoinedAt(LocalDateTime.now());
        participantRepository.save(participant);

        if (participantRepository.countByGroupBoardId(groupBoardId) == board.getTotalUsers()) {
            board.setBoardStatus(BoardStatus.CLOSED);
            groupBoardRepository.save(board);

            ChatRoom chatRoom = chatRoomRepository.findByGroupBoard(board)
                    .orElseThrow();
            chatRoomService.updateChatRoomStatus(chatRoom.getId());

            // TODO: 상태가 RECRUITED가 됐을 때만 시스템 메시지 전송
//            if (chatRoom.getStatus() == ChatRoomStatus.RECRUITED) {
//                chatMessageService.sendSystemMessage(
//                        chatRoom.getId(),
//                        "모든 인원이 모였습니다. 결제를 진행해주세요."
//                );
//            }
        }
    }

    public void pay(Long participantId) {
        Participant p = participantRepository.findById(participantId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        p.setPaymentStatus(PaymentStatus.PAID);
        participantRepository.save(p);

        ChatRoom chatRoom = chatRoomRepository
                .findByGroupBoard(p.getGroupBoard())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        chatRoomService.updateChatRoomStatus(chatRoom.getId());
    }

    public void tradeComplete(Long participantId) {
        Participant p = participantRepository.findById(participantId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        p.setTradeCompleted(true);
        participantRepository.save(p);

        ChatRoom chatRoom = chatRoomRepository
                .findByGroupBoard(p.getGroupBoard())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        chatRoomService.updateChatRoomStatus(chatRoom.getId());

        GroupBoard board = p.getGroupBoard();
        List<Participant> list = participantRepository.findAllByChatRoomId(board.getId());
        boolean allTraded = list.stream().allMatch(Participant::isTradeCompleted);
        if (allTraded) {
            board.setBoardStatus(BoardStatus.COMPLETED);
            groupBoardRepository.save(board);
        }

    }

    //TODO: 리뷰작성
//    public Review review(Long participantId, ReviewDto dto) {
//        Review r = new Review();
//        r.setParticipant(participantRepository.findById(participantId).orElseThrow());
//        r.setContent(dto.getContent());
//        r.setRating(dto.getRating());
//        reviewRepository.save(r);
//
//        Long groupBoardId = r.getParticipant().getChatRoom().getGroupBoard().getId();
//        GroupBoard board = groupBoardRepository.findById(groupBoardId)
//                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
//        board.setBoardStatus(BoardStatus.SUCCESS);
//        groupBoardRepository.save(board);
//
//        Long chatRoomId = r.getParticipant().getChatRoom().getId();
//        chatRoomService.updateChatRoomStatus(chatRoomId);
//
//        return r;
//    }

}
