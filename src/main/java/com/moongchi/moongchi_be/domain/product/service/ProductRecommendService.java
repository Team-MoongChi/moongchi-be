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

        List<?> raw = (List<?>) redisTemplate.opsForValue().get(redisKey);

        List<Long> productIds = new ArrayList<>();
        if (raw != null) {
            for (Object o : raw) {
                if (o instanceof Integer) {
                    productIds.add(((Integer) o).longValue());
                } else if (o instanceof Long) {
                    productIds.add((Long) o);
                }
            }
        }
        if (productIds.isEmpty()) {
            String url = apiUrl.endsWith("/") ? apiUrl + userId : apiUrl + "/" + userId;
            ResponseEntity<MlopsRecommendResponse> resp =
                    restTemplate.getForEntity(url, MlopsRecommendResponse.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                RecommendProductResponse body = resp.getBody().getData();  // getData() 아님

                List<String> strIds = body.getRecommendedItemIds();
                List<Long> ids = (strIds == null)
                        ? Collections.emptyList()
                        :strIds.stream().map(Long::valueOf).collect(Collectors.toList());

                if (ids != null && !ids.isEmpty()) {
                    productIds = ids;
                    redisTemplate.opsForValue()
                            .set(redisKey, productIds, Duration.ofDays(1));
                } else {
                    redisTemplate.delete(redisKey);
                }
            }
        }

        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Product> products = productRepository.findAllById(productIds);

        Map<Long, Product> map = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<ProductResponseDto> dtos = productIds.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());

        return dtos;
    }
}
