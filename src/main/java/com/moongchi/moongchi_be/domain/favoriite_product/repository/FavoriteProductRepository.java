package com.moongchi.moongchi_be.domain.favoriite_product.repository;

import com.moongchi.moongchi_be.domain.favoriite_product.entity.FavoriteProduct;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
    boolean existsByUserAndGroupBoard(User user, GroupBoard groupBoard);
    Optional<FavoriteProduct> findByUserAndGroupBoard(User user, GroupBoard groupBoard);
    Optional<FavoriteProduct> findByUser(User user);

    List<FavoriteProduct> findAllByUser(User user);
    List<FavoriteProduct> findByGroupBoard(GroupBoard groupBoard);
}
