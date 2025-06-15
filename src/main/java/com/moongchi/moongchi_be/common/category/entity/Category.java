package com.moongchi.moongchi_be.common.category.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "categories")
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name="large_category", nullable = false)
    private String largeCategory;

    @Column(name = "medium_category")
    private String mediumCategory;

    @Column(name = "small_category")
    private String smallCategory;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
