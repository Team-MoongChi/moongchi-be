package com.moongchi.moongchi_be.domain.user.repository;

import com.moongchi.moongchi_be.domain.user.entity.MannerPercent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MannerPercentRepository extends JpaRepository<MannerPercent, Long> {
}
