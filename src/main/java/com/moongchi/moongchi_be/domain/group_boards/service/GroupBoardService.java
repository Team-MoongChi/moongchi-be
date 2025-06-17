package com.moongchi.moongchi_be.domain.group_boards.service;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.repository.CategoryRepository;
import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.dto.BoardParticipantDto;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoom;
import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import com.moongchi.moongchi_be.domain.chat.entity.PaymentStatus;
import com.moongchi.moongchi_be.domain.chat.entity.Role;
import com.moongchi.moongchi_be.domain.chat.repository.ChatRoomRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import com.moongchi.moongchi_be.domain.chat.service.ChatRoomService;
import com.moongchi.moongchi_be.domain.favoriite_product.repository.FavoriteProductRepository;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardRequestDto;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupProduct;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupProductRepository;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import com.moongchi.moongchi_be.domain.product.repository.ProductRepository;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.repository.UserRepository;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupBoardService {
    private final GroupBoardRepository groupBoardRepository;
    private final GroupProductRepository groupProductRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FavoriteProductRepository favoriteProductRepository;
    private final UserService userService;
    private final KakaoMapService kakaoMapService;
    private final ChatRoomService chatRoomService;

    @Transactional
    public void createPost(GroupBoardRequestDto dto, User user) {
        Coordinate coordinate = kakaoMapService.getCoordinateFromAddress(dto.getLocation());

        GroupBoard groupBoard = GroupBoard.builder()
                .title(dto.getName() + "  " + dto.getQuantity() + " 공구합니다.")
                .location(dto.getLocation())
                .latitude(coordinate.getLatitude())
                .longitude(coordinate.getLongitude())
                .content(dto.getContent())
                .boardStatus(BoardStatus.OPEN)
                .deadline(dto.getDeadline())
                .totalUsers(dto.getTotalUser())
                .user(user)
                .build();

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        }

        Product product = null;
        if (dto.getProductId() != null) {
            product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        }

        if (product != null && category == null) {
            category = product.getCategory();
        }

        GroupProduct groupProduct = GroupProduct.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .category(category)
                .product(product)
                .images(dto.getImages() == null ? Collections.singletonList(product.getImgUrl()): dto.getImages())
                .build();

        groupBoard.updateGroupProduct(groupProduct);
        groupBoardRepository.save(groupBoard);
        chatRoomService.createChatRoomWithParticipant(groupBoard, user);
    }

    @Transactional
    public void updatePost(Long group_board_id, GroupBoardRequestDto dto) {
        GroupBoard groupBoard = groupBoardRepository.findById(group_board_id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        GroupProduct groupProduct = groupProductRepository.findById(groupBoard.getGroupProduct().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        groupProduct.update(dto.getName(), dto.getPrice(), dto.getQuantity(), category, dto.getImages());
        groupBoard.update(dto.getName(), dto.getContent(), dto.getLocation(), dto.getDeadline(), dto.getTotalUser(), groupProduct);

        groupBoardRepository.save(groupBoard);
    }

    @Transactional
    public void deletePost(Long group_board_id) {
        GroupBoard groupBoard = groupBoardRepository.findById(group_board_id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        groupBoardRepository.delete(groupBoard);
    }

    @Transactional(readOnly = true)
    public List<GroupBoardListDto> getGroupBoardList( User user) {
        List<GroupBoard> groupBoards = groupBoardRepository.findNearbyPosts(user.getLatitude(), user.getLongitude());

        return groupBoards.stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());
    }

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

        if(board.getTotalUsers() - (participantRepository.countByGroupBoardId(groupBoardId) + 1) == 1) {
            board.updateStatus(BoardStatus.CLOSING_SOON);
            groupBoardRepository.save(board);
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
            board.updateStatus(BoardStatus.CLOSED);
            groupBoardRepository.save(board);

            ChatRoom chatRoom = chatRoomRepository.findByGroupBoard(board)
                    .orElseThrow();
            chatRoomService.updateChatRoomStatus(chatRoom.getId());

        }
    }

    @Transactional(readOnly = true)
    public GroupBoardDto getGroupBoard(Long groupBoardId,  User user) {
        GroupBoard groupBoard = groupBoardRepository.findById(groupBoardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Long currentUserId = user.getId();
        return convertToDto(groupBoard, currentUserId);
    }

    @Transactional(readOnly = true)
    public List<GroupBoardListDto> getMyGroupBoard( User user) {
        List<GroupBoard> groupBoards = groupBoardRepository.findByUserId(user.getId());

        return groupBoards.stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupBoardListDto> getGroupBoardCategory(Long categoryId,  User user) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        List<GroupBoard> groupBoards = groupBoardRepository.findNearbyPostsByCategory(user.getLatitude(), user.getLongitude(), category.getLargeCategory());

        return groupBoards.stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupBoardListDto> getUserGroupBoard(Long userId) {
        List<GroupBoard> groupBoards = groupBoardRepository.findByUserId(userId);

        return groupBoards.stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupBoardDto> getProductGroupBoardList(Long productId) {
        List<GroupBoard> groupBoards = groupBoardRepository.findByProductId(productId);

        return groupBoards.stream()
                .map(this::convertToParticipanDto)
                .collect(Collectors.toList());
    }

    //사진 총원 총수량 상품명 총가격 장소 모집마감 날짜 카테고리
    public GroupBoardDto getEditGroupBoard(Long groupBoardId){
        GroupBoard groupBoard = groupBoardRepository.findById(groupBoardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long largeCategoryId = 0L;
        if (groupBoard.getGroupProduct().getProduct() != null) {
            Category largeCategory = categoryRepository.findByLargeCategoryAndMediumCategoryIsNullAndSmallCategoryIsNull(
                            groupBoard.getGroupProduct().getProduct().getCategory().getLargeCategory())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
            largeCategoryId = largeCategory.getId();
        } else {
            largeCategoryId = groupBoard.getGroupProduct().getCategory().getId();
        }

        return GroupBoardDto.builder()
                .totalUser(groupBoard.getTotalUsers())
                .quantity(groupBoard.getGroupProduct().getQuantity())
                .name(groupBoard.getGroupProduct().getName())
                .price(groupBoard.getGroupProduct().getPrice())
                .location(groupBoard.getLocation())
                .deadline(groupBoard.getDeadline())
                .categoryId(largeCategoryId)
                .images(groupBoard.getGroupProduct().getImages())
                .content(groupBoard.getContent())
                .build();
    }

    public int getLikeCount(Long groupBoardId){
        return favoriteProductRepository.countByGroupBoardId(groupBoardId);
    }

    private GroupBoardDto convertToParticipanDto(GroupBoard board) {
        List<BoardParticipantDto> participants = new ArrayList<>();
        if (board.getParticipants() != null) {
            participants = board.getParticipants().stream()
                    .map(p -> {
                        User user = p.getUser();
                        return BoardParticipantDto.builder()
                                .userId(user.getId())
                                .profileUrl(user.getProfileUrl())
                                .nickname(p.getRole() == Role.LEADER ? user.getNickname():null)
                                .role(p.getRole().toString())
                                .mannerLeader(p.getRole() == Role.LEADER ? user.getMannerPercent().getLeaderPercent() : null)
                                .build();
                    }).collect(Collectors.toList());

        }

        return GroupBoardDto.builder()
                .id(board.getId())
                .totalUser(board.getTotalUsers())
                .currentUsers(participants.size())
                .participants(participants)
                .deadline(board.getDeadline())
                .build();
    }

    public GroupBoardListDto convertToListDto(GroupBoard board) {
        GroupProduct groupProduct = board.getGroupProduct();
        Product product = groupProduct.getProduct();

        List<BoardParticipantDto> participants = new ArrayList<>();
        if (board.getParticipants() != null) {
            participants = board.getParticipants().stream()
                    .map(p -> {
                        User user = p.getUser();
                        return BoardParticipantDto.builder()
                                .id(p.getId())
                                .userId(user.getId())
                                .nickname(user.getNickname())
                                .profileUrl(user.getProfileUrl())
                                .build();
                    }).collect(Collectors.toList());
        }

        String imageUrl = (product != null)
                ? product.getImgUrl()
                : (!groupProduct.getImages().isEmpty() ? groupProduct.getImages().get(0) : null);


        return GroupBoardListDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .price(groupProduct.getPrice())
                .location(board.getLocation())
                .boardStatus(board.getBoardStatus())
                .image(imageUrl)
                .createAt(board.getCreatedAt())
                .totalUsers(board.getTotalUsers())
                .currentUsers(participants.size())
                .participants(participants)
                .build();
    }


    private GroupBoardDto convertToDto(GroupBoard board, Long currentUserId) {

        GroupProduct groupProduct = board.getGroupProduct();
        Product product = groupProduct.getProduct();

        List<BoardParticipantDto> participants = new ArrayList<>();
        if (board.getParticipants() != null) {
            participants = board.getParticipants().stream()
                    .map(p -> {
                        User user = p.getUser();
                        return BoardParticipantDto.builder()
                                .userId(user.getId())
                                .nickname(user.getNickname())
                                .profileUrl(user.getProfileUrl())
                                .mannerLeader(p.getRole() == Role.LEADER ? user.getMannerPercent().getLeaderPercent() : null)
                                .role(p.getRole().toString())
                                .build();
                    }).collect(Collectors.toList());
        }

        boolean editable = board.getUser().getId().equals(currentUserId);
        int likeCount = getLikeCount(board.getId());
        return GroupBoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .price(groupProduct.getPrice())
                .content(board.getContent())
                .location(board.getLocation())
                .boardStatus(board.getBoardStatus())
                .deadline(board.getDeadline())
                .totalUser(board.getTotalUsers())
                .currentUsers(participants.size())
                .productName(product != null ? product.getName() : null)
                .productPrice(product != null ? product.getPrice() : null)
                .productUrl(product != null ? product.getProductUrl() : null)
                .chatRoomId(board.getChatRoom().getId())
                .likeCount(likeCount)
                .editable(editable)
                .images(product != null ? Collections.singletonList(product.getImgUrl()) : groupProduct.getImages())
                .participants(participants)
                .build();
    }

}
