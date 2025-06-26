package com.moongchi.moongchi_be.domain.product.service;

import com.moongchi.moongchi_be.domain.product.dto.MlopsRecommendResponse;
import com.moongchi.moongchi_be.domain.product.dto.ProductResponseDto;
import com.moongchi.moongchi_be.domain.product.dto.RecommendProductResponse;
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

        List<Long> productIds = new ArrayList<>();
        Object raw = redisTemplate.opsForValue().get(redisKey);
        if (raw instanceof List<?> rawList) {
            productIds = rawList.stream()
                    .filter(o -> o instanceof Number)
                    .map(o -> ((Number) o).longValue())
                    .collect(Collectors.toList());
        }

        if (productIds.isEmpty()) {
            redisTemplate.delete(redisKey);

            try {
                String url = apiUrl.endsWith("/") ? apiUrl + userId : apiUrl + "/" + userId;
                ResponseEntity<MlopsRecommendResponse> resp =
                        restTemplate.getForEntity(url, MlopsRecommendResponse.class);

                if (resp.getStatusCode().is2xxSuccessful()
                        && resp.getBody() != null
                        && resp.getBody().getData() != null) {

                    RecommendProductResponse body = resp.getBody().getData();
                    List<String> strIds = body.getRecommendedItemIds();
                    List<Long> ids = (strIds == null)
                            ? Collections.emptyList()
                            : strIds.stream().map(Long::valueOf).collect(Collectors.toList());

                    if (!ids.isEmpty()) {
                        productIds = ids;
                        redisTemplate.opsForValue()
                                .set(redisKey, productIds, Duration.ofDays(1));
                    } else {
                        log.info("추천 API 빈 리스트 반환, 캐시 삭제: {}", redisKey);
                        redisTemplate.delete(redisKey);
                    }
                }
            } catch (RestClientException e) {
                log.error("추천 API 호출 실패: userId={}", userId, e);
            }
        }

        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> map = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return productIds.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }
}
