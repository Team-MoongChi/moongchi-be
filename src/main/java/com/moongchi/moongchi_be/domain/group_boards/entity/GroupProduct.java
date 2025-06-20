package com.moongchi.moongchi_be.domain.group_boards.entity;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "group_products")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_product_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(mappedBy = "groupProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private GroupBoard groupBoard;

    @ElementCollection
    private List<String> images;

    public void updateGroupBoard(GroupBoard groupBoard) {
        this.groupBoard = groupBoard;
    }

    public void update(String name, int price, String quantity, Category category, List<String> images) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.images = images;
    }

}
