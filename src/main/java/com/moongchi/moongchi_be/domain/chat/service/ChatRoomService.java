package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.dto.*;
import com.moongchi.moongchi_be.domain.chat.entity.*;
import com.moongchi.moongchi_be.domain.chat.repository.ChatMessageRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ChatRoomRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ReviewRepository;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupProduct;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import com.moongchi.moongchi_be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GroupBoardRepository groupBoardRepository;
    private final ReviewRepository reviewRepository;
    private final ChatMessageService chatMessageService;

    //채팅방 조회
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getUserChatRooms(Long userId) {
        List<Participant> participants = participantRepository.findByUserId(userId);

        return participants.stream()
                .map(Participant::getGroupBoard)
                .map(GroupBoard::getChatRoom)
                .filter(Objects::nonNull)
                .map(chatRoom -> {
                    GroupBoard board = chatRoom.getGroupBoard();
                    GroupProduct product = board.getGroupProduct();

                    String title = (product != null)
                            ? product.getName() + " " + product.getQuantity() + " 공구방"
                            : "상품 정보 없음 공구방";

                    String imgUrl = (product != null && product.getImages() != null && !product.getImages().isEmpty())
                            ? product.getImages().get(0)
                            : null;
                    Optional<ChatMessage> lastMessageOpt =
                            chatMessageRepository.findFirstByChatRoomIdOrderBySendAtDesc(chatRoom.getId());
                    String lastMessage = lastMessageOpt.map(ChatMessage::getMessage).orElse(null);
                    LocalDateTime lastMessageTime = lastMessageOpt.map(ChatMessage::getSendAt).orElse(null);

                    return ChatRoomResponseDto.builder()
                            .id(chatRoom.getId())
                            .title(title)
                            .status(chatRoom.getStatus().getKorean())
                            .imgUrl(imgUrl)
                            .participantCount(participantRepository.countByGroupBoardId(board.getId()))
                            .lastMessage(lastMessage)
                            .lastMessageTime(lastMessageTime)
                            .unreadCount(0)
                            .build();
                })

                .collect(Collectors.toList());
    }

    //채팅방 상세조회
    @Transactional(readOnly = true)
    public ChatRoomDetailDto getChatRoomDetail(Long chatRoomId,Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        GroupProduct product = chatRoom.getGroupBoard().getGroupProduct();
        String imgUrl = null;
        int price = 0;

        if (product != null && product.getImages() != null && !product.getImages().isEmpty()) {
            imgUrl = product.getImages().get(0);
            price = product.getPrice();
        }

        int totalUsers = chatRoom.getGroupBoard().getTotalUsers();
        int perPersonPrice = (totalUsers > 0)
                ? (price / totalUsers)
                : 0;

        List<ParticipantDto> participants = participantRepository.findAllByChatRoomId(chatRoomId).stream()
                .map(p -> {

                            boolean isMe = p.getUser().getId().equals(userId);
                            boolean reviewed = false;
                            if(!isMe) {
                                reviewed = reviewRepository.existsByParticipantIdAndGroupBoardId(p.getId(), p.getGroupBoard().getId());
                            }

                          return new ParticipantDto(
                                    p.getId(),
                                    p.getUser().getId(),
                                    p.getUser().getNickname(),
                                    p.getUser().getProfileUrl(),
                                    p.getRole().toString(),
                                    p.getPaymentStatus().toString(),
                                    p.isTradeCompleted(),
                                    perPersonPrice,
                                    isMe,
                                    reviewed
                            );
                        })
                .collect(Collectors.toList());

        List<MessageDto> messages = chatMessageRepository
                .findByChatRoomIdOrderBySendAtAsc(chatRoomId)
                .stream()
                .map(m -> new MessageDto(
                        m.getId(),
                        m.getParticipantId(),
                        m.getMessage(),
                        m.getMessageType().name(),
                        m.getSendAt()
                ))
                .collect(Collectors.toList());

        return new ChatRoomDetailDto(
                chatRoom.getId(),
                chatRoom.getTitle(),
                chatRoom.getStatus().getKorean(),
                imgUrl,
                price,
                chatRoom.getGroupBoard().getDeadline(),
                participants,
                messages
        );
    }


    // 채팅방 생성
    @Transactional
    public ChatRoom createChatRoomWithParticipant(GroupBoard groupBoard, User creator) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setTitle(groupBoard.getGroupProduct().getName() + " " + groupBoard.getGroupProduct().getQuantity() + " 공구방" );
        chatRoom.setStatus(ChatRoomStatus.RECRUITING);
        chatRoom.setGroupBoard(groupBoard);
        chatRoom.setCreatedAt(LocalDateTime.now());
        chatRoom.setUpdatedAt(LocalDateTime.now());

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        Participant participant = Participant.builder()
                .groupBoard(groupBoard)
                .user(creator)
                .role(Role.LEADER)
                .tradeCompleted(true)
                .joinedAt(LocalDateTime.now())
                .paymentStatus(PaymentStatus.PAID)
                .build();
        participantRepository.save(participant);
        String welcomeMsg = "안녕하세요! 공구 완료 시점까지 여러분과 함께 할 뭉치예요. 뭉치면 산다! 공구 인원이 모두 모이면 알려줄게요.";
        sendSystemMessage(savedChatRoom.getId(), welcomeMsg);

        savedChatRoom.setSendAt(LocalDateTime.now());
        chatRoomRepository.save(savedChatRoom);

        return savedChatRoom;
    }

    public ChatRoom getChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    //채팅방 상태 업데이트
    @Transactional
    public ChatRoomStatus updateChatRoomStatus(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        GroupBoard groupBoard = chatRoom.getGroupBoard();
        int requiredParticipants = groupBoard.getTotalUsers();
        int currentParticipants = participantRepository.countByChatRoomId(chatRoomId);

        List<Participant> participants = participantRepository.findAllByChatRoomId(chatRoomId);
        boolean allPaid = participants.stream().allMatch(p -> p.getPaymentStatus() == PaymentStatus.PAID);
        boolean anyPaid = participants.stream().anyMatch(p -> p.getPaymentStatus() == PaymentStatus.PAID);
        boolean allTraded = participants.stream().allMatch(Participant::isTradeCompleted);

        ChatRoomStatus prev = chatRoom.getStatus();
        ChatRoomStatus next = prev;

        switch (prev) {

            case RECRUITING:
                if (currentParticipants == requiredParticipants) {
                    next = ChatRoomStatus.RECRUITED;
                }
                break;
            case RECRUITED:
            case PAYING:
                if (allPaid) {
                    next = ChatRoomStatus.PURCHASED;
                } else if (anyPaid) {
                    next = ChatRoomStatus.PAYING;
                }
                break;

            case PURCHASED:
                if (allTraded) {
                    next = ChatRoomStatus.COMPLETED;
                }
                break;

            default:
                break;
        }

        if (next != prev) {
            chatRoom.setStatus(next);
            chatRoomRepository.save(chatRoom);

            // 상태별 메시지 내용 생성 (예시)
            String msg = switch (next) {
                case RECRUITED -> "모집이 완료되었습니다! 결제를 진행해 주세요.";
                case PAYING -> "일부 결제가 완료되었습니다. 나머지 인원도 결제해 주세요.";
                case PURCHASED -> "공동구매가 완료되었습니다! 거래 장소/시간을 공지해 주세요.";
                case COMPLETED -> "거래가 모두 완료되었습니다. 리뷰를 작성해 보세요!";
                default -> "채팅방 상태가 " + next.name() + "로 변경되었습니다.";
            };
            sendSystemMessage(chatRoomId, msg);
        }
        return next;


    }

    public void sendSystemMessage(Long chatRoomId, String message) {
        ChatMessage systemMsg = ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .participantId(null)
                .message(message)
                .messageType(MessageType.SYSTEM)
                .build();
        chatMessageRepository.save(systemMsg);
    }

    public void pay(Long chatRoomId, Long userId) {
        Participant participant = participantRepository
                .findByChatRoomIdAndUserId(chatRoomId,userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        participant.setPaymentStatus(PaymentStatus.PAID);
        participantRepository.save(participant);

        ChatRoom chatRoom = chatRoomRepository
                .findByGroupBoard(participant.getGroupBoard())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        updateChatRoomStatus(chatRoom.getId());
    }

    public void tradeComplete(Long chatRoomId, Long userId) {
        Participant participant = participantRepository
                .findByChatRoomIdAndUserId(chatRoomId,userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        participant.setTradeCompleted(true);
        participantRepository.save(participant);

        ChatRoom chatRoom = chatRoomRepository
                .findByGroupBoard(participant.getGroupBoard())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        updateChatRoomStatus(chatRoom.getId());

        List<Participant> list = participantRepository.findAllByChatRoomId(participant.getGroupBoard().getId());
        boolean allTraded = list.stream().allMatch(Participant::isTradeCompleted);
        if (allTraded) {
            participant.getGroupBoard().setBoardStatus(BoardStatus.COMPLETED);
            groupBoardRepository.save(participant.getGroupBoard());
        }

    }

    public ReviewResponseDto writeReviewByChatRoom(Long chatRoomId, Long userId, Long targetParticipantId, ReviewRequestDto dto) {
        Participant writerParticipant = participantRepository
                .findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Participant targetParticipant = participantRepository
                .findById(targetParticipantId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (userId.equals(targetParticipant.getUser().getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (reviewRepository.existsByTargetAndWriter(
                writerParticipant.getGroupBoard().getId(), targetParticipantId, userId)) {
            throw new CustomException(ErrorCode.CONFLICT);
        }

        Review review = new Review();
        review.setStar(dto.getStar());
        review.setKeyword(dto.getKeywords().stream().toList().toString());
        review.setReview(dto.getReview());
        review.setCreatedAt(LocalDateTime.now());
        review.setParticipant(targetParticipant);
        review.setGroupBoard(targetParticipant.getGroupBoard());

        reviewRepository.save(review);

        return new ReviewResponseDto(
                review.getId(),
                review.getStar(),
                review.getKeyword(),
                review.getReview(),
                review.getParticipant().getId(),
                review.getGroupBoard().getId(),
                review.getCreatedAt()
        );
    }

}
