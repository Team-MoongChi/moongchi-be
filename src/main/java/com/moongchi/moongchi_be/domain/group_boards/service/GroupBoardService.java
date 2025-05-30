package com.moongchi.moongchi_be.domain.group_boards.service;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.common.category.repository.CategoryRepository;
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

@Service
@RequiredArgsConstructor
public class GroupBoardService {
    private final GroupBoardRepository groupBoardRepository;
    private final GroupProductRepository groupProductRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public void createPost(GroupBoardRequestDto dto, HttpServletRequest request) {
        User currentUser = userService.getUser(request).get();
        System.out.println(currentUser);
        GroupBoard groupBoard = GroupBoard.builder()
                .title(dto.getName() + " 공구합니다.")
                .location(dto.getLocation())
                .content(dto.getContent())
                .boardStatus(BoardStatus.OPEN)
                .deadline(dto.getDeadLine())
                .totalUsers(dto.getTotalUsers())
                .user(currentUser)
                .build();

        Category category = categoryRepository.findById(dto.getCategoryId()).get();
        Product product = null;
        if (dto.getProductId() != null) {
            product = productRepository.findById(dto.getProductId())
                    .orElse(null);
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
    }

    public void updatePost(Long group_board_id, GroupBoardRequestDto dto) {
        GroupBoard groupBoard = groupBoardRepository.findById(group_board_id).get();
        GroupProduct groupProduct = groupProductRepository.findById(groupBoard.getGroupProduct().getId()).get();

        Category category = categoryRepository.findById(dto.getCategoryId()).get();

        groupProduct.update(dto.getName(), dto.getPrice(), dto.getQuantity(), category);
        groupBoard.update(dto.getName(), dto.getContent(), dto.getLocation(), dto.getDeadLine(), dto.getTotalUsers(), groupProduct);

        groupBoardRepository.save(groupBoard);
    }

    public void deletePost(Long group_board_id) {
        GroupBoard groupBoard = groupBoardRepository.findById(group_board_id).get();
        groupBoardRepository.delete(groupBoard);
    }

}
