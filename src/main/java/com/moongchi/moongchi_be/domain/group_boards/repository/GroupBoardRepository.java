package com.moongchi.moongchi_be.domain.group_boards.repository;

import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupBoardRepository extends JpaRepository<GroupBoard, Long> {
}
