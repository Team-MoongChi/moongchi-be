package com.moongchi.moongchi_be.domain.group_boards.entity;

import com.moongchi.moongchi_be.domain.chat.entity.ChatRoom;
import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import com.moongchi.moongchi_be.domain.favoriite_product.entity.FavoriteProduct;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import com.moongchi.moongchi_be.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "group_boards")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;

    @Column(nullable = false)
    private String location;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BoardStatus boardStatus;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(name = "total_users", nullable = false)
    private int totalUsers;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "group_product_id", nullable = false)
    private GroupProduct groupProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "groupBoard",cascade = CascadeType.ALL, orphanRemoval = true)
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "groupBoard", cascade = CascadeType.ALL)
    private List<FavoriteProduct> favoriteProducts;

    @OneToMany(mappedBy = "groupBoard")
    private List<Participant> participants;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    //== 비즈니스 로직 ==//
    public void updateGroupProduct(GroupProduct groupProduct) {
        this.groupProduct = groupProduct;
        groupProduct.updateGroupBoard(this);
    }

    public void update(String title, String content, String location, double latitude, double longitude, LocalDate deadline, int totalUsers, GroupProduct groupProduct) {
        this.title = title + "공구합니다.";
        this.content = content;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deadline = deadline;
        this.totalUsers = totalUsers;
        this.groupProduct = groupProduct;
    }

    public void updateStatus(BoardStatus boardStatus){
        this.boardStatus = boardStatus;
    }

}
