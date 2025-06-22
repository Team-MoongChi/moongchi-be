package com.moongchi.moongchi_be.domain.group_boards.service;

import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.enums.BoardStatus;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupBoardScheduler {

    private final GroupBoardRepository groupBoardRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateStatus(){
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plusDays(1);
        LocalDate dayAfterTomorrow = now.plusDays(2);

        // 모집중 -> 마감임박
        List<GroupBoard> toClosingSoon = groupBoardRepository.findByBoardStatusAndDeadlineBetween(BoardStatus.OPEN, now, tomorrow);

        for (GroupBoard board : toClosingSoon) {
            board.updateStatus(BoardStatus.CLOSING_SOON);
        }

        // 마감임박 -> 모집마감
        List<GroupBoard> toClosed = groupBoardRepository.findByBoardStatusAndDeadlineBefore(BoardStatus.CLOSING_SOON, dayAfterTomorrow);

        for (GroupBoard board : toClosed){
            board.updateStatus(BoardStatus.CLOSED);
        }

        groupBoardRepository.saveAll(toClosingSoon);
        groupBoardRepository.saveAll(toClosed);
    }
}
