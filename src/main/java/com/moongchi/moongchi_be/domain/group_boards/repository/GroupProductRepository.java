package com.moongchi.moongchi_be.domain.group_boards.repository;

import com.moongchi.moongchi_be.domain.group_boards.entity.GroupProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupProductRepository extends JpaRepository<GroupProduct, Long> {
}
