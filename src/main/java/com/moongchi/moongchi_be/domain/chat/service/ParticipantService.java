package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.entity.*;
import com.moongchi.moongchi_be.domain.chat.repository.ChatRoomRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import com.moongchi.moongchi_be.domain.user.repository.UserRepository;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final GroupBoardRepository groupBoardRepository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

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

        if (currentCount + 1 == board.getTotalUsers()) {
            board.setBoardStatus(BoardStatus.CLOSED);

            ChatRoom chatRoom = chatRoomRepository.findByGroupBoard(board)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            chatRoom.setStatus(ChatRoomStatus.RECRUITED);
            chatRoomRepository.save(chatRoom); // 저장 반영

//            // system 메시지 전송
//            chatMessageService.sendSystemMessage(chatRoom.getId(),
//                    "모든 인원이 모였습니다. 결제를 진행해주세요.");
        }
    }
    

}
