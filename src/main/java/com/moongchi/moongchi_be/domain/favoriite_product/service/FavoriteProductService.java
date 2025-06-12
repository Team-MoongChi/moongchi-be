package com.moongchi.moongchi_be.domain.favoriite_product.service;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.favoriite_product.entity.FavoriteProduct;
import com.moongchi.moongchi_be.domain.favoriite_product.repository.FavoriteProductRepository;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import com.moongchi.moongchi_be.domain.group_boards.service.GroupBoardService;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteProductService {
    private final FavoriteProductRepository favoriteProductRepository;
    private final UserService userService;
    private final GroupBoardRepository groupBoardRepository;
    private final GroupBoardService groupBoardService;

    public void addLike(Long groupBoardId, HttpServletRequest request){
        User user  = userService.getUser(request);
        GroupBoard groupBoard = groupBoardRepository.findById(groupBoardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        boolean isExist = favoriteProductRepository.existsByUserAndGroupBoard(user, groupBoard);
        if (isExist){
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        FavoriteProduct favoriteProduct = FavoriteProduct.builder()
                .user(user)
                .groupBoard(groupBoard)
                .build();

        favoriteProductRepository.save(favoriteProduct);
    }

    public void removeLike(Long groupBoardId, HttpServletRequest request){
        User user  = userService.getUser(request);
        GroupBoard groupBoard = groupBoardRepository.findById(groupBoardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        boolean isExist = favoriteProductRepository.existsByUserAndGroupBoard(user, groupBoard);
        if (!isExist){
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        FavoriteProduct favoriteProduct = favoriteProductRepository.findByUserAndGroupBoard(user, groupBoard)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        favoriteProductRepository.delete(favoriteProduct);
    }

    public List<GroupBoardListDto> getLikes(HttpServletRequest request){
        User user = userService.getUser(request);
        List<FavoriteProduct> favoriteProducts = favoriteProductRepository.findAllByUser(user);

        List<GroupBoardListDto> groupBoardListDtos = favoriteProducts.stream()
                .map(fav -> groupBoardService.convertToListDto(fav.getGroupBoard()))
                .collect(Collectors.toList());

        return groupBoardListDtos;
    }
}
