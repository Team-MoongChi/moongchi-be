package com.moongchi.moongchi_be.domain.product.entity;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.domain.group_product.entity.GroupProduct;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(length = 20)
    private String name;

    private int price;

    @Column(name = "img_url",length = 250)
    private String imgUrl;

    @Column(name = "product_url", length = 250)
    private String productUrl;

    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupProduct> groupProducts;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


}
