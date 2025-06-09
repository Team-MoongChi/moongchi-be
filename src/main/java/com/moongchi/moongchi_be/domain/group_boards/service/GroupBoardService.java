package com.moongchi.moongchi_be.domain.group_boards.service;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.repository.CategoryRepository;
import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.dto.BoardParticipantDto;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoom;
import com.moongchi.moongchi_be.domain.chat.entity.Role;
import com.moongchi.moongchi_be.domain.chat.service.ChatRoomService;
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
import com.moongchi.moongchi_be.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserService userService;
    private final KakaoMapService kakaoMapService;
    private final ChatRoomService chatRoomService;

    @Transactional
    public void createPost(GroupBoardRequestDto dto, HttpServletRequest request) {
        User currentUser = userService.getUser(request);
        Coordinate coordinate = kakaoMapService.getCoordinateFromAddress(dto.getLocation());

        GroupBoard groupBoard = GroupBoard.builder()
                .title(dto.getName() + " 공구합니다.")
                .location(dto.getLocation())
                .latitude(coordinate.getLatitude())
                .longitude(coordinate.getLongitude())
                .content(dto.getContent())
                .boardStatus(BoardStatus.OPEN)
                .deadline(dto.getDeadLine())
                .totalUsers(dto.getTotalUsers())
                .user(currentUser)
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
        chatRoomService.createChatRoomWithParticipant(groupBoard, currentUser);
    }

    @Transactional
    public void updatePost(Long group_board_id, GroupBoardRequestDto dto) {
        GroupBoard groupBoard = groupBoardRepository.findById(group_board_id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        GroupProduct groupProduct = groupProductRepository.findById(groupBoard.getGroupProduct().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        groupProduct.update(dto.getName(), dto.getPrice(), dto.getQuantity(), category);
        groupBoard.update(dto.getName(), dto.getContent(), dto.getLocation(), dto.getDeadLine(), dto.getTotalUsers(), groupProduct);

        groupBoardRepository.save(groupBoard);
    }

    @Transactional
    public void deletePost(Long group_board_id) {
        GroupBoard groupBoard = groupBoardRepository.findById(group_board_id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        groupBoardRepository.delete(groupBoard);
    }

    @Transactional(readOnly = true)
    public List<GroupBoardListDto> getGroupBoardList(HttpServletRequest request) {
        User currentUser = userService.getUser(request);
        List<GroupBoard> groupBoards = groupBoardRepository.findNearbyPosts(currentUser.getLatitude(), currentUser.getLongitude());

        return groupBoards.stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroupBoardDto getGroupBoard(Long groupBoardId) {
        GroupBoard groupBoard = groupBoardRepository.findById(groupBoardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        return convertToDto(groupBoard);
    }

    @Transactional(readOnly = true)
    public List<GroupBoardListDto> getMyGroupBoard(HttpServletRequest request) {
        User currentUser = userService.getUser(request);
        List<GroupBoard> groupBoards = groupBoardRepository.findByUserId(currentUser.getId());

        return groupBoards.stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupBoardListDto> getGroupBoardCategory(Long categoryId) {
        List<GroupBoard> groupBoards = groupBoardRepository.findByCategoryId(categoryId);

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

    private GroupBoardDto convertToParticipanDto(GroupBoard board) {
        List<BoardParticipantDto> participants = new ArrayList<>();
        if (board.getParticipants() != null) {
            participants = board.getParticipants().stream()
                    .map(p -> {
                        User user = p.getUser();
                        return BoardParticipantDto.builder()
                                .userId(user.getId())
                                .profileUrl(user.getProfileUrl())
                                .mannerLeader(p.getRole() == Role.LEADER ? user.getMannerPercent().getLeaderPercent() : null)
                                .role(p.getRole().toString())
                                .build();
                    }).collect(Collectors.toList());

        }

        return GroupBoardDto.builder()
                .id(board.getId())
                .totalUsers(board.getTotalUsers())
                .currentUsers(participants.size())
                .participants(participants)
                .deadline(board.getDeadline())
                .build();
    }

    private GroupBoardListDto convertToListDto(GroupBoard board) {
        GroupProduct groupProduct = board.getGroupProduct();
        Product product = groupProduct.getProduct();

        List<BoardParticipantDto> participants = new ArrayList<>();
        ChatRoom chatRoom = board.getChatRoom();
        if (chatRoom != null && chatRoom.getGroupBoard().getParticipants() != null) {
            participants = chatRoom.getGroupBoard().getParticipants().stream()
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
                .createAt(board.getCreateAt())
                .totalUsers(board.getTotalUsers())
                .currentUsers(participants.size())
                .participants(participants)
                .build();
    }


    private GroupBoardDto convertToDto(GroupBoard board) {

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

        return GroupBoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .price(groupProduct.getPrice())
                .content(board.getContent())
                .location(board.getLocation())
                .boardStatus(board.getBoardStatus().toString())
                .deadline(board.getDeadline())
                .totalUsers(board.getTotalUsers())
                .currentUsers(participants.size())
                .productUrl(product != null ? product.getProductUrl() : null)
                .images(product != null ? Collections.singletonList(product.getImgUrl()) : groupProduct.getImages())
                .participants(participants)
                .build();
    }

}
