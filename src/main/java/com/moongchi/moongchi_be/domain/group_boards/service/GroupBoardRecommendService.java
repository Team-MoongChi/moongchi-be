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
        List<Long> groupBoardIds = new ArrayList<>();

        if (rawList != null) {
            for (Object obj : rawList) {
                if (obj instanceof Integer) {
                    groupBoardIds.add(((Integer) obj).longValue());
                } else if (obj instanceof Long) {
                    groupBoardIds.add((Long) obj);
                }
            }
        }

        System.out.println(groupBoardIds);

        if(groupBoardIds.isEmpty()){
            String url = apiUrl + userId;
            ResponseEntity<GroupBoardRecommendDto> response = restTemplate.getForEntity(url, GroupBoardRecommendDto.class);
            System.out.println(response);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Collections.emptyList();
            }

            groupBoardIds = response.getBody().getData().getPopularGroups().stream()
                    .map(dto -> dto.getGroupId().longValue())
                    .collect(Collectors.toList());

            System.out.println(groupBoardIds);

            redisTemplate.opsForValue().set(redisKey, groupBoardIds, Duration.ofDays(1));
        }

        List<GroupBoard> groupBoards = groupBoardRepository.findAllById(groupBoardIds);
        Map<Long, GroupBoard> boardMap = groupBoards.stream()
                .collect(Collectors.toMap(GroupBoard::getId, Function.identity()));

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

    public GroupBoardListDto convertToListDto(GroupBoard board) {
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