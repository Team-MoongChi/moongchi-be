package com.moongchi.moongchi_be.domain.group_product.entity;

import com.moongchi.moongchi_be.domain.group_product.enums.BoardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String location;

    @Column(name = "board_status")
    private BoardStatus boardStatus;

    @Column
    private LocalDate deadline;

    @Column(name = "total_users")
    private int totalUsers;

    @Column(name = "create_at")
    @CreationTimestamp
    private LocalDateTime createAt;

    @Column(name="update_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "group_product_id")
    private GroupProduct groupProduct;
}
