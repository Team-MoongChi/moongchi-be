package com.moongchi.moongchi_be.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.lang.Nullable;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GroupBoardRepository groupBoardRepository;
    private final ReviewRepository reviewRepository;
    private final ChatMessageService chatMessageService;

    @Value("${IMP_KEY}")
    private String impKey;

    @Value("${IMP_API_SECRET}")
    private String impSecretKey;


    //채팅방 조회
    public List<ChatRoomResponseDto> getUserChatRooms(Long userId) {
        List<Participant> participants = participantRepository.findByUserId(userId);

        return participants.stream()
                .filter(p -> p.getGroupBoard().getChatRoom() != null)
                .map(Participant::getGroupBoard)
                .map(GroupBoard::getChatRoom)
                .map(chatRoom -> {
                    GroupBoard board = chatRoom.getGroupBoard();
                    GroupProduct product = board.getGroupProduct();

                    String title = (product != null)
                            ? product.getName() + " " + product.getQuantity() + " 공구방"
                            : "상품 정보 없음 공구방";

                    String imgUrl = null;
                    if (product != null) {
                        if (product.getProduct() != null && product.getProduct().getImgUrl() != null) {
                            imgUrl = product.getProduct().getImgUrl();
                        }
                        else if (product.getImages() != null && !product.getImages().isEmpty()) {
                            imgUrl = product.getImages().get(0);
                        }
                    }

                    Optional<ChatMessage> lastMessageOpt =
                            chatMessageRepository.findFirstByChatRoomIdOrderBySendAtDesc(chatRoom.getId());
                    String lastMessage = lastMessageOpt.map(ChatMessage::getMessage).orElse(null);
                    LocalDateTime lastMessageTime = lastMessageOpt.map(ChatMessage::getSendAt).orElse(null);

                    Participant participant = board.getParticipants().stream()
                            .filter(p -> p.getUser().getId().equals(userId))
                            .findFirst()
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                    LocalDateTime lastReadAt = participant.getReadAt();
                    long unreadCount = (lastReadAt == null)
                            ? chatMessageRepository.countByChatRoomId(chatRoom.getId())
                            : chatMessageRepository.countByChatRoomIdAndSendAtAfter(chatRoom.getId(), lastReadAt);


                    return ChatRoomResponseDto.builder()
                            .id(chatRoom.getId())
                            .title(title)
                            .status(chatRoom.getStatus().getKorean())
                            .imgUrl(imgUrl)
                            .participantCount(participantRepository.countByGroupBoardId(board.getId()))
                            .lastMessage(lastMessage)
                            .lastMessageTime(lastMessageTime)
                            .unreadCount((int)unreadCount)
                            .build();
                })

                .sorted(Comparator.comparing(ChatRoomResponseDto::getLastMessageTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    //채팅방 상세조회
    public ChatRoomDetailDto getChatRoomDetail(Long chatRoomId, Long userId, @Nullable LocalDateTime before, int size) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        GroupProduct product = chatRoom.getGroupBoard().getGroupProduct();
        String imgUrl = null;
        int price = 0;

        if (product != null) {
            if (product.getProduct() != null && product.getProduct().getImgUrl() != null) {
                imgUrl = product.getProduct().getImgUrl();
            }
            else if (product.getImages() != null && !product.getImages().isEmpty()) {
                imgUrl = product.getImages().get(0);

            }
        }

        int totalUsers = chatRoom.getGroupBoard().getTotalUsers();
        int perPersonPrice = (totalUsers > 0)
                ? (product.getPrice() / totalUsers)
                : 0;

        List<ParticipantDto> participants = participantRepository.findAllByChatRoomId(chatRoomId).stream()
                .map(p -> {

                    boolean isMe = p.getUser().getId().equals(userId);
                    boolean reviewed = false;
                    if (!isMe) {
                        reviewed = reviewRepository.existsByParticipantIdAndParticipantGroupBoardId(p.getId(), p.getGroupBoard().getId());
                    }

                    return new ParticipantDto(
                            p.getId(),
                            p.getUser().getId(),
                            p.getUser().getNickname(),
                            p.getUser().getProfileUrl(),
                            p.getRole().toString(),
                            p.getPaymentStatus().toString(),
                            p.getJoinedAt(),
                            p.isTradeCompleted(),
                            perPersonPrice,
                            isMe,
                            reviewed
                    );
                })
                .collect(Collectors.toList());


        List<MessageDto> messages;
        if (before != null) {
            Pageable pageable = PageRequest.of(0, size);
            Slice<ChatMessage> slice = chatMessageRepository
                    .findByChatRoomIdAndSendAtBeforeOrderBySendAtDesc(chatRoomId, before, pageable);

            messages = slice.getContent().stream()
                    .map(m -> mapToDto(m, chatRoom.getStatus(), userId))
                    .collect(Collectors.toList());
            Collections.reverse(messages);

        } else {
            messages = chatMessageRepository
                    .findByChatRoomIdOrderBySendAtAsc(chatRoomId)
                    .stream()
                    .map(m -> mapToDto(m, chatRoom.getStatus(), userId))
                    .collect(Collectors.toList());
        }

        return new ChatRoomDetailDto(
                chatRoom.getId(),
                chatRoom.getGroupBoard().getId(),
                chatRoom.getTitle(),
                chatRoom.getStatus().getKorean(),
                imgUrl,
                product.getPrice(),
                chatRoom.getGroupBoard().getDeadline(),
                chatRoom.getGroupBoard().getLocation(),
                participants,
                messages
        );
    }

    private MessageDto mapToDto(ChatMessage m, ChatRoomStatus roomStatus, Long userId) {
        if (m.getMessageType() == MessageType.SYSTEM) {
            String chatStatus = null, buttonVisibleTo = null;
            String content = m.getMessage();
            if (content.contains("안녕하세요!")) {
                chatStatus = "RECRUITING"; buttonVisibleTo = "ALL";
            } else if (content.contains("결제를 진행해 주세요")) {
                chatStatus = "RECRUITED";  buttonVisibleTo = "ALL";
            } else if (content.contains("결제가 모두 완료")) {
                chatStatus = "PAYING";     buttonVisibleTo = "LEADER";
            } else if (content.contains("구매가 완료되었어요!")) {
                chatStatus = "PURCHASED";  buttonVisibleTo = "ALL";
            } else if (content.contains("리뷰를 남겨")) {
                chatStatus = "COMPLETED";  buttonVisibleTo = "ALL";
            }
            return MessageDto.from(m, roomStatus, chatStatus, buttonVisibleTo);
        }
        return MessageDto.from(m);
    }


    // 채팅방 생성
    @Transactional
    public ChatRoom createChatRoomWithParticipant(GroupBoard groupBoard, User creator) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setTitle(groupBoard.getGroupProduct().getName() + " " + groupBoard.getGroupProduct().getQuantity() + " 공구방");
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
        
        String welcomeMsg = "안녕하세요! 공구 완료 시점까지 여러분과 함께 할 뭉치예요. 뭉치면 산다!";
        chatMessageService.sendSystemMessage(savedChatRoom.getId(), welcomeMsg, ChatRoomStatus.RECRUITING, "RECRUITING", "ALL");

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
                if (allPaid) next = ChatRoomStatus.PAYING;
                break;
            case PAYING:
                break;
            case PURCHASED:
                if (allTraded) {
                    next = ChatRoomStatus.COMPLETED;
                }
                break;
        }

        if (next != prev) {
            chatRoom.setStatus(next);
            chatRoomRepository.save(chatRoom);

            // 상태별 시스템 메시지 전송
            switch (next) {
                case RECRUITED:
                    chatMessageService.sendSystemMessage(chatRoomId, "모집이 완료되었습니다! 결제를 진행해 주세요.",
                            ChatRoomStatus.RECRUITED, "RECRUITED", "ALL");
                    break;

                case PAYING:
                    chatMessageService.sendSystemMessage(chatRoomId, "결제가 모두 완료 되었어요. 방장님 물품을 구매 후 구매 완료를 눌러주세요!",
                            ChatRoomStatus.PAYING, "PAYING", "LEADER");
                    break;

                case PURCHASED:
                    break;

                case COMPLETED:
                    chatMessageService.sendSystemMessage(chatRoomId, "공구가 완료 되었습니다. 팀원들과 리뷰를 남겨주세요!",
                            ChatRoomStatus.COMPLETED, "COMPLETED", "ALL");
                    break;
            }
        }

        return next;
    }

    public ChatRoomDetailDto getChatRoomDetail(Long chatRoomId, Long userId) {
        return getChatRoomDetail(chatRoomId, userId, null, Integer.MAX_VALUE);
    }

    //거래중
    public void pay(Long chatRoomId, Long userId, String impUid) {
        ChatRoomDetailDto chatRoomDetailDto = getChatRoomDetail(chatRoomId, userId);
        int perPersonPrice = chatRoomDetailDto.getParticipants().stream()
                .filter(ParticipantDto::isMe)
                .findFirst()
                .map(ParticipantDto::getPerPersonPrice)
                .orElse(0);


        if (!verifyPayment(impUid, perPersonPrice)) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        Participant participant = participantRepository
                .findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        participant.setPaymentStatus(PaymentStatus.PAID);
        participantRepository.save(participant);

        ChatRoom chatRoom = chatRoomRepository
                .findByGroupBoard(participant.getGroupBoard())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        updateChatRoomStatus(chatRoom.getId());
    }
    
    //구매 완료
    public void markAsPurchased(Long chatRoomId, Long userId) {
        Participant leader = participantRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (leader.getRole() != Role.LEADER) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (chatRoom.getStatus() != ChatRoomStatus.PAYING) {
            throw new CustomException(ErrorCode.CONFLICT);
        }

        chatRoom.setStatus(ChatRoomStatus.PURCHASED);
        chatRoomRepository.save(chatRoom);

        chatMessageService.sendSystemMessage(
                chatRoomId,
                "구매가 완료되었어요! 거래 장소와 시간을 채팅으로 정해주세요.",
                ChatRoomStatus.PURCHASED,
                "PURCHASED",
                "ALL"
        );
    }

    //거래완료
    public void tradeComplete(Long chatRoomId, Long userId) {
        Participant participant = participantRepository
                .findByChatRoomIdAndUserId(chatRoomId, userId)
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
            participant.getGroupBoard().updateStatus(BoardStatus.COMPLETED);
            groupBoardRepository.save(participant.getGroupBoard());
        }

    }

    //리뷰작성
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
        review.setKeywords(dto.getKeywords());
        review.setReview(dto.getReview());
        review.setCreatedAt(LocalDateTime.now());
        review.setParticipant(targetParticipant);
        review.getParticipant().setGroupBoard(targetParticipant.getGroupBoard());

        reviewRepository.save(review);

        return new ReviewResponseDto(
                review.getId(),
                review.getStar(),
                review.getKeywords(),
                review.getReview(),
                review.getParticipant().getId(),
                review.getParticipant().getGroupBoard().getId(),
                review.getCreatedAt()
        );
    }

    public boolean verifyPayment(String impUid, int expectedAmount) {
        String accessToken = getAccessToken();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.iamport.kr/payments/" + impUid,
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> paymentData = (Map<String, Object>) response.getBody().get("response");

        int amount = (int) paymentData.get("amount");
        String status = (String) paymentData.get("status");

        return status.equals("paid") && amount == expectedAmount;
    }

    public String getAccessToken() {
        String url = "https://api.iamport.kr/users/getToken";

        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = new HashMap<>();
        body.put("imp_key", impKey);
        body.put("imp_secret", impSecretKey);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<PortOneResponseDto> response = restTemplate.postForEntity(url, entity,
                PortOneResponseDto.class);

        PortOneResponseDto responseBody = response.getBody();
        if (responseBody == null || responseBody.getCode() != 0) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        return response.getBody().getResponse().getAccess_token();

    }

    @Transactional
    public void leaveChatRoom(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (chatRoom.getStatus() != ChatRoomStatus.COMPLETED) {
            throw new CustomException(ErrorCode.CONFLICT);
        }

        Participant participant = participantRepository
                .findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONFLICT));

        String nickname = participant.getUser().getNickname();
        chatMessageService.publishLeaveEvent(chatRoomId, nickname);

        participantRepository.delete(participant);

        long remaining = participantRepository.countByChatRoomId(chatRoomId);
        if (remaining == 0) {
            chatRoomRepository.delete(chatRoom);
        }
    }

}
