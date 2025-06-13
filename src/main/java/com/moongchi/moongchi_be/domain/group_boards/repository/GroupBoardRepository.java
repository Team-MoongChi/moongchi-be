package com.moongchi.moongchi_be.domain.group_boards.repository;

import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupBoardRepository extends JpaRepository<GroupBoard, Long> {
    @Query(value = "SELECT * FROM group_boards g WHERE " +
            "(6371 * acos(cos(radians(:userLat)) * cos(radians(g.latitude)) * cos(radians(g.longitude) - radians(:userLng)) + sin(radians(:userLat)) * sin(radians(g.latitude)))) < 0.75",
            nativeQuery = true)
    List<GroupBoard> findNearbyPosts(@Param("userLat") double userLat, @Param("userLng") double userLng);

    @Query(value = "SELECT gb.* FROM group_boards gb " +
            "JOIN group_products gp ON gb.group_product_id = gp.group_product_id " +
            "JOIN categories c ON gp.category_id = c.category_id " +
            "WHERE c.large_category = :largeCategory " +
            "AND (6371 * acos(cos(radians(:userLat)) * cos(radians(gb.latitude)) * " +
            "cos(radians(gb.longitude) - radians(:userLng)) + sin(radians(:userLat)) * sin(radians(gb.latitude)))) < 0.75",
            nativeQuery = true)
    List<GroupBoard> findNearbyPostsByCategory(@Param("userLat") double userLat,
                                                    @Param("userLng") double userLng,
                                                    @Param("largeCategory") String largeCategory);

    List<GroupBoard> findByUserId(Long userId);

    List<GroupBoard> findByTitleContaining(String keyword);
    @Query("SELECT gb FROM GroupBoard gb WHERE gb.groupProduct.product.id = :productId")
    List<GroupBoard> findByProductId(@Param("productId") Long productId);
   
    Optional<GroupBoard> findByChatRoomId(Long chatRoomId);
}