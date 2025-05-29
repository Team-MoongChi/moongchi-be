package com.moongchi.moongchi_be.domain.product.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
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

    private Double rating;

    @Column(name = "product_url", length = 250)
    private String productUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "category_id", length = 20, nullable = false)
    private String categoryId;


}
