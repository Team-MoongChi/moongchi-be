package com.moongchi.moongchi_be.domain.product.service;

import com.moongchi.moongchi_be.domain.product.dto.ProductsRecommendDto;
import com.moongchi.moongchi_be.domain.product.dto.ProductResponseDto;
import com.moongchi.moongchi_be.domain.product.entity.Product;
import com.moongchi.moongchi_be.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRecommendService {

    @Value("${RECOMMEND_PRODUCT_URL}")
    private String apiUrl;

    @Value("${RECOMMEND_PRODUCT_KEY_PREFIX}")
    private String recommendKeyPrefix;

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;

    public List<ProductResponseDto> getRecommendProducts(Long userId) {
        String redisKey = recommendKeyPrefix + userId;

        Object cached = redisTemplate.opsForValue().get(redisKey);
        List<Long> productIds = new ArrayList<>();
        if (cached instanceof List<?>) {
            productIds = ((List<?>) cached).stream()
                    .filter(e -> e instanceof Number)
                    .map(e -> ((Number) e).longValue())
                    .collect(Collectors.toList());
        }

        if (cached == null || productIds.isEmpty()) {
            redisTemplate.delete(redisKey);
            try {
                String url = apiUrl.endsWith("/") ? apiUrl + userId : apiUrl + "/" + userId;
                ResponseEntity<ProductsRecommendDto> response =
                        restTemplate.getForEntity(url, ProductsRecommendDto.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    List<String> rawIds = response.getBody().getData().getRecommendedItemIds();
                    if (rawIds != null && !rawIds.isEmpty()) {
                        productIds = rawIds.stream()
                                .map(Long::valueOf)
                                .collect(Collectors.toList());
                        // 캐시 저장 (TTL 1일)
                        redisTemplate.opsForValue().set(redisKey, productIds, Duration.ofDays(1));
                    } else {
                        log.info("MLOps API가 빈 추천 리스트를 반환했습니다: {}", redisKey);
                    }
                } else {
                    log.warn("MLOps API 비정상 응답 상태: {} for user {}", response.getStatusCode(), userId);
                }
            } catch (RestClientException e) {
                log.error("MLOps API 호출 중 예외 발생: userId={}", userId, e);
            }
        }

        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return productIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }
}
