package com.moongchi.moongchi_be.domain.group_boards.service;

import com.moongchi.moongchi_be.common.category.dto.CategoryResponseDto;
import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.repository.CategoryRepository;
import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.dto.ParticipantDto;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoom;
import com.moongchi.moongchi_be.domain.chat.entity.Role;
import com.moongchi.moongchi_be.domain.chat.service.ChatRoomService;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardRequestDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupProductDto;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupProduct;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupProductRepository;
import com.moongchi.moongchi_be.domain.product.dto.ProductDto;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import com.moongchi.moongchi_be.domain.product.repository.ProductRepository;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
                .images(dto.getImages())
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
    public List<GroupBoardDto> getGroupBoardList(HttpServletRequest request) {
        User currentUser = userService.getUser(request);
        List<GroupBoard> groupBoards = groupBoardRepository.findNearbyPosts(currentUser.getLatitude(), currentUser.getLongitude());

        return groupBoards.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroupBoardDto getGroupBoard(Long groupBoardId) {
        GroupBoard groupBoard = groupBoardRepository.findById(groupBoardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        return convertToDto(groupBoard);
    }

    @Transactional(readOnly = true)
    public List<GroupBoardDto> getMyGroupBoard(HttpServletRequest request) {
        User currentUser = userService.getUser(request);
        List<GroupBoard> groupBoards = groupBoardRepository.findByUserId(currentUser.getId());

        return groupBoards.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupBoardDto> getGroupBoardCategory(Long categoryId) {
        List<GroupBoard> groupBoards = groupBoardRepository.findByCategoryId(categoryId);

        return groupBoards.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupBoardDto> getUserGroupBoard(Long userId) {
        List<GroupBoard> groupBoards = groupBoardRepository.findByUserId(userId);

        return groupBoards.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private GroupBoardDto convertToDto(GroupBoard board) {

        GroupProduct groupProduct = board.getGroupProduct();
        Product product = groupProduct.getProduct();
        Category category = groupProduct.getCategory();

        if (groupProduct == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        if (category == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        CategoryResponseDto categoryDto = CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .level(category.getLevel())
                .build();

        ProductDto productDto = null;
        if (product != null) {
            productDto = ProductDto.builder()
                    .id(product.getId())
                    .imgUrl(product.getImgUrl())
                    .productUrl(product.getProductUrl())
                    .build();
        }

        GroupProductDto groupProductDto = GroupProductDto.builder()
                .id(groupProduct.getId())
                .name(groupProduct.getName())
                .price(groupProduct.getPrice())
                .quantity(groupProduct.getQuantity())
                .images(groupProduct.getImages())
                .category(categoryDto)
                .product(productDto)
                .build();

        List<ParticipantDto> participants = new ArrayList<>();
        ChatRoom chatRoom = board.getChatRoom();
        if (chatRoom != null && chatRoom.getParticipants() != null) {
            participants = chatRoom.getParticipants().stream()
                    .map(p -> {
                        User user = p.getUser();
                        return ParticipantDto.builder()
                                .id(p.getId())
                                .userId(user.getId())
                                .nickname(user.getNickname())
                                .profileUrl(user.getProfileUrl())
                                .mannerLeader(p.getRole() == Role.LEADER ? user.getMannerLeader() : null)
                                .role(p.getRole().toString())
                                .build();
                    }).collect(Collectors.toList());
        }

        if (board.getTotalUsers() - participants.size() == 1){
            board.updateStatus(BoardStatus.CLOSING_SOON);
        }

        return GroupBoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .location(board.getLocation())
                .boardStatus(board.getBoardStatus())
                .deadline(board.getDeadline())
                .totalUsers(board.getTotalUsers())
                .currentUsers(participants.size())
//                .groupProduct(groupProductDto)
                .participants(participants)
                .build();
    }

}
