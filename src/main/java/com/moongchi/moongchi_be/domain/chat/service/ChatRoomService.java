package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.dto.ChatRoomDetailDto;
import com.moongchi.moongchi_be.domain.chat.dto.ChatRoomResponseDto;
import com.moongchi.moongchi_be.domain.chat.dto.MessageDto;
import com.moongchi.moongchi_be.domain.chat.dto.ParticipantDto;
import com.moongchi.moongchi_be.domain.chat.entity.*;
import com.moongchi.moongchi_be.domain.chat.repository.ChatMessageRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ChatRoomRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageService chatMessageService;

    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getUserChatRooms(Long userId) {
        List<Participant> participants = participantRepository.findByUserId(userId);

        return participants.stream()
                .map(Participant::getChatRoom)
                .distinct()
                .map(chatRoom -> {
                    int participantCount = participantRepository.countByChatRoomId(chatRoom.getId());

                    return ChatRoomResponseDto.builder()
                            .id(chatRoom.getId())
                            .title(chatRoom.getTitle())
                            .status(chatRoom.getStatus())
                            .participantCount(participantCount)
                            .imgUrl(List.of(chatRoom.getGroupBoard().getGroupProduct().getImages()).toString())
                            .lastMessage(null)  // 메시지 로직은 아직 구현 안 됐으니 null 처리
                            .lastMessageTime(null)
                            .unreadCount(0)  // 추후 구현
                            .createdAt(chatRoom.getCreatedAt())
                            .updatedAt(chatRoom.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatRoomDetailDto getChatRoomDetail(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 참여자 목록 조회
        List<ParticipantDto> participants = participantRepository.findByChatRoomId(chatRoomId).stream()
                .map(p -> new ParticipantDto(
                        p.getUser().getId(),
                        p.getUser().getNickname(),
                        p.getUser().getProfileUrl()
                )).collect(Collectors.toList());

        // 메시지 목록 조회
        List<MessageDto> messages = chatMessageRepository.findByChatRoomId(chatRoomId).stream()
                .map(m -> new MessageDto(
                        m.getId(),
                        m.getUserId(),
                        m.getMessage(),
                        m.getMessageType().name(),
                        m.getSendAt()
                )).collect(Collectors.toList());

        return new ChatRoomDetailDto(
                chatRoom.getId(),
                chatRoom.getTitle(),
                chatRoom.getStatus().getKorean(),
                participants,
                messages
        );
    }

    @Transactional
    public ChatRoom createChatRoomWithParticipant(GroupBoard groupBoard, User creator) {
        // 1. 채팅방 생성
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setTitle(groupBoard.getTitle());
        chatRoom.setStatus(ChatRoomStatus.RECRUITING);
        chatRoom.setGroupBoard(groupBoard);
        chatRoom.setCreatedAt(LocalDateTime.now());
        chatRoom.setUpdatedAt(LocalDateTime.now());

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        Participant participant = Participant.builder()
                .chatRoom(savedChatRoom)
                .groupBoard(groupBoard)
                .user(creator)
                .role(Role.LEADER)
                .joinedAt(LocalDateTime.now())
                .paymentStatus(PaymentStatus.PAID)
                .build();
        participantRepository.save(participant);

        return savedChatRoom;
    }

    //TODO : 시스템 메시지 사용
    @Transactional
    public void updateChatRoomStatus(Long chatRoomId, ChatRoomStatus status) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        chatRoom.setStatus(status);
        chatRoomRepository.save(chatRoom);

        String systemMessage = getSystemMessageForStatus(status);

        chatMessageService.sendSystemMessage(
                chatRoom.getId().toString(),
                systemMessage
        );
    }

    private String getSystemMessageForStatus(ChatRoomStatus status) {
        return switch (status) {
            case RECRUITING -> "공구가 시작 되었습니다. 참여자를 모집 중입니다.";
            case RECRUITED -> "모집이 완료 되었습니다. 각 참여자들은 결제를 진행해주세요.";
            case PAYING -> "결제가 시작되었습니다. n분의 1 결제를 완료해주세요.";
            case PURCHASED -> "물품 구매가 완료되었습니다. 오프라인 만남을 진행해주세요.";
            case COMPLETED -> "공구가 완료되었습니다. 리뷰를 남겨주세요.";
        };
    }



}
