package com.moongchi.moongchi_be.domain.group_product.repository;

import com.moongchi.moongchi_be.domain.group_product.entity.GroupProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupProductRepository extends JpaRepository<GroupProduct, Long> {
}
