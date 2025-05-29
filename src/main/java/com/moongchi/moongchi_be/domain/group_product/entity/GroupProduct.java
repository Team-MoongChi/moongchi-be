package com.moongchi.moongchi_be.domain.group_product.entity;

import com.moongchi.moongchi_be.common.category.entity.Category;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column
    private String name;

    @Column
    private int price;

    @Column
    private String quantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "product_image", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages;


}
