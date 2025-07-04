package com.moongchi.moongchi_be.domain.favoriite_product.repository;

import com.moongchi.moongchi_be.domain.favoriite_product.entity.FavoriteProduct;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import com.moongchi.moongchi_be.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
    boolean existsByUserAndGroupBoard(User user, GroupBoard groupBoard);
    boolean existsByUserAndProduct(User user, Product product);

    Optional<FavoriteProduct> findByUserAndProduct(User user, Product product);
    Optional<FavoriteProduct> findByUserAndGroupBoard(User user, GroupBoard groupBoard);
    Optional<FavoriteProduct> findByUser(User user);

    int countByGroupBoardId(Long groupBoardId);
    int countByProductId(Long productId);

    List<FavoriteProduct> findAllByUserAndProductIsNull(User user);
    List<FavoriteProduct> findAllByUserAndGroupBoardIsNull(User user);
}
