package com.moongchi.moongchi_be.domain.group_boards.service;

import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupBoardRecommendScheduler {

    private final GroupBoardRecommendService groupBoardRecommendService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 1 * * *")
    public void updateRecommend() {
        List<User> users = userRepository.findAllByIdGreaterThanEqual200();

        for (User user : users) {
            groupBoardRecommendService.asyncUpdateRecommendCache(user.getId());
        }

        System.out.println("추천 캐시 갱신");
    }
}
