package com.moongchi.moongchi_be.domain.group_boards.repository;

import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface GroupBoardRepository extends JpaRepository<GroupBoard, Long> {
    @Query(value = "SELECT * FROM group_boards g WHERE " +
            "(6371 * acos(cos(radians(:userLat)) * cos(radians(g.latitude)) * cos(radians(g.longitude) - radians(:userLng)) + sin(radians(:userLat)) * sin(radians(g.latitude)))) < 0.75",
            nativeQuery = true)
    List<GroupBoard> findNearbyPosts(@Param("userLat") double userLat, @Param("userLng") double userLng);

    @Query(value = "SELECT g.* FROM group_boards g " +
            "JOIN group_products gp ON g.group_product_id = gp.product_id " +
            "WHERE gp.category_id = :categoryId AND " +
            "(6371 * acos(cos(radians(:userLat)) * cos(radians(g.latitude)) * " +
            "cos(radians(g.longitude) - radians(:userLng)) + " +
            "sin(radians(:userLat)) * sin(radians(g.latitude)))) < 0.75",
            nativeQuery = true)
                                                    @Param("userLat") double userLat,
                                                    @Param("userLng") double userLng);

    List<GroupBoard> findByUserId(Long userId);

//    @Query("SELECT gb FROM GroupBoard gb WHERE gb.groupProduct.category.id = :categoryId")
//    List<GroupBoard> findByCategoryId(@Param("categoryId") Long categoryId);
}
