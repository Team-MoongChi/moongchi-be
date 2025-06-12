package com.moongchi.moongchi_be.domain.favoriite_product.service;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.favoriite_product.entity.FavoriteProduct;
import com.moongchi.moongchi_be.domain.favoriite_product.repository.FavoriteProductRepository;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import com.moongchi.moongchi_be.domain.group_boards.service.GroupBoardService;
import com.moongchi.moongchi_be.domain.product.dto.ProductResponseDto;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import com.moongchi.moongchi_be.domain.product.repository.ProductRepository;
import com.moongchi.moongchi_be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteProductService {
    private final FavoriteProductRepository favoriteProductRepository;
    private final GroupBoardRepository groupBoardRepository;
    private final ProductRepository productRepository;
    private final GroupBoardService groupBoardService;

    public void addLike(Long groupBoardId, User user){
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

    public void removeLike(Long groupBoardId, User user){
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

    public List<GroupBoardListDto> getLikes(User user){
        List<FavoriteProduct> favoriteProducts = favoriteProductRepository.findAllByUserAndProductIsNull(user);

        List<GroupBoardListDto> groupBoardListDtos = favoriteProducts.stream()
                .map(fav -> groupBoardService.convertToListDto(fav.getGroupBoard()))
                .collect(Collectors.toList());

        return groupBoardListDtos;
    }

    public void addProductLike(Long productId, User user){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        boolean isExist = favoriteProductRepository.existsByUserAndProduct(user, product);
        if (isExist){
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        FavoriteProduct favoriteProduct = FavoriteProduct.builder()
                .user(user)
                .product(product)
                .build();

        favoriteProductRepository.save(favoriteProduct);
    }

    public void removeProductLike(Long productId, User user){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        boolean isExist = favoriteProductRepository.existsByUserAndProduct(user, product);
        if (!isExist){
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        FavoriteProduct favoriteProduct = favoriteProductRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        favoriteProductRepository.delete(favoriteProduct);
    }

    public List<ProductResponseDto> getProductLikes(User user){
        List<FavoriteProduct> favoriteProducts = favoriteProductRepository.findAllByUserAndGroupBoardIsNull(user);

        List<ProductResponseDto> productResponseDtoList = favoriteProducts.stream()
                .map(fav -> ProductResponseDto.from(fav.getProduct()))
                .collect(Collectors.toList());

        return productResponseDtoList;
    }



}
