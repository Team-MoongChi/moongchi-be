package com.moongchi.moongchi_be.domain.product.repository;

import com.moongchi.moongchi_be.domain.product.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory,Long> {
    List<SearchHistory> findByUserIdOrderBySearchAtDesc(Long userId);
}
