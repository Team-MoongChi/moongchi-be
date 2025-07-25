package com.moongchi.moongchi_be.domain.group_boards.service;

import com.moongchi.moongchi_be.domain.chat.dto.BoardParticipantDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardListDto;
import com.moongchi.moongchi_be.domain.group_boards.dto.GroupBoardRecommendDto;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.group_boards.entity.GroupProduct;
import com.moongchi.moongchi_be.domain.group_boards.repository.GroupBoardRepository;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import com.moongchi.moongchi_be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupBoardRecommendService {

    @Value("${RECOMMEND_GROUP_URL}")
    private String apiUrl;

    @Value("${RECOMMEND_KEY_PREFIX}")
    private String recommendKeyPrefix;

    private final RestTemplate restTemplate;
    private final GroupBoardRepository groupBoardRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<GroupBoardListDto> getRecommendGroupBoard(Long userId) {
        String redisKey = recommendKeyPrefix + userId;

        List<?> rawList = (List<?>) redisTemplate.opsForValue().get(redisKey);
        List<Long> groupBoardIds = convertToLongs(rawList);

        if (groupBoardIds.isEmpty()){
            groupBoardIds = updateRecommendCache(userId, redisKey);
        }

        List<GroupBoard> groupBoards = groupBoardRepository.findAllById(groupBoardIds);
        Map<Long, GroupBoard> boardMap = groupBoards.stream()
                .collect(Collectors.toMap(GroupBoard::getId, Function.identity()));

        System.out.println(groupBoardIds);
        for (Long id : groupBoardIds) {
            if (!boardMap.containsKey(id)) {
                System.out.println("groupBoard ID {} not found in DB" + id);
            }
        }

        return groupBoardIds.stream()
                .map(boardMap::get)
                .filter(Objects::nonNull)
                .map(this::convertToListDto)
                .collect(Collectors.toList());
    }

    private List<Long> updateRecommendCache(Long userId, String redisKey){
        String url = apiUrl + userId;
        ResponseEntity<GroupBoardRecommendDto> response = restTemplate.getForEntity(url, GroupBoardRecommendDto.class);

        GroupBoardRecommendDto body = response.getBody();

        if (!response.getStatusCode().is2xxSuccessful() || body == null || body.getData() == null || body.getData().getPopularGroups() == null) {
            System.out.println("추천 API 응답 비정상: userId = " + userId + ", body = " + body);
            return Collections.emptyList();
        }

        List<Long> groupBoardIds = body.getData().getPopularGroups().stream()
                    .map(dto -> dto.getGroupId().longValue())
                    .collect(Collectors.toList());

        redisTemplate.opsForValue().set(redisKey, groupBoardIds, Duration.ofDays(1));
        return groupBoardIds;
    }

    @Async
    public void asyncUpdateRecommendCache(Long userId){
        String redisKey = recommendKeyPrefix + userId;
        updateRecommendCache(userId, redisKey);
    }

    private List<Long> convertToLongs(List<?> rawList){
        if (rawList == null) return new ArrayList<>();
        List<Long> result = new ArrayList<>();
        for (Object obj : rawList) {
            if (obj instanceof Integer) {
                result.add(((Integer) obj).longValue());
            } else if (obj instanceof Long) {
                result.add((Long) obj);
            }
        }
        return result;
    }

    private GroupBoardListDto convertToListDto(GroupBoard board) {
        GroupProduct groupProduct = board.getGroupProduct();
        Product product = groupProduct.getProduct();

        List<BoardParticipantDto> participants = new ArrayList<>();
        if (board.getParticipants() != null) {
            participants = board.getParticipants().stream()
                    .map(p -> {
                        User user = p.getUser();
                        return BoardParticipantDto.builder()
                                .userId(user.getId())
                                .profileUrl(user.getProfileUrl())
                                .build();
                    }).collect(Collectors.toList());
        }

        String imageUrl = (product != null)
                ? product.getImgUrl()
                : (!groupProduct.getImages().isEmpty() ? groupProduct.getImages().get(0) : null);


        return GroupBoardListDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .boardStatus(board.getBoardStatus())
                .image(imageUrl)
                .totalUsers(board.getTotalUsers())
                .currentUsers(participants.size())
                .participants(participants)
                .build();
    }
}