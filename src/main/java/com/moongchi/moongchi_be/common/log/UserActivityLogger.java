package com.moongchi.moongchi_be.common.log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moongchi.moongchi_be.common.category.dto.CategoryResponseDto;
import com.moongchi.moongchi_be.common.category.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivityLogger {

    private final CategoryService categoryService;

    @Around("@annotation(logEvent)")
    public Object logWithAnnotation(ProceedingJoinPoint joinPoint, LogEvent logEvent) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        ObjectMapper objectMapper = new ObjectMapper();

        String eventType = logEvent.value();
        Object result = joinPoint.proceed();

        Map<String, Object> message = new HashMap<>();
        message.put("event_type", eventType);
        message.put("user_id", getUsernameFromSecurity());
        message.put("session_id", request.getSession().getId());

        // api/products/search?keyword=
        if("search".equals(eventType)){
            String keyword = request.getParameter("keyword");
            int resultCount = 0;
            if (result instanceof ResponseEntity<?> response) {
                Object body = response.getBody();
                if (body instanceof List<?> list) {
                    resultCount = list.size();
                }
            }
            message.put("search_keyword", keyword);
            message.put("searched_at", Instant.now().toString());
            message.put("search_result_count", resultCount);
        } else if("click".equals(eventType)){
            String uri = request.getRequestURI();
            message.put("clicked_at", Instant.now().toString());

            if(uri.matches("/api/products/\\d+")){
                Object rawBody = ((ResponseEntity<?>) result).getBody();
                Map<String, Object> body = objectMapper.convertValue(rawBody, new TypeReference<>() {});

                message.put("item_id", body.get("id"));
                message.put("item_name",body.get("name"));
                message.put("clicked_at", Instant.now().toString());
                String category = body.get("largeCategory") + " > " + body.get("mediumCategory") + " > " + body.get("smallCategory");
                message.put("item_category_id", body.get("categoryId"));
                message.put("item_category", category);
                message.put("item_price", body.get("price"));

            } else if(uri.matches("/api/products/categories/\\d+")){
                UriTemplate template = new UriTemplate("/api/products/categories/{categoryId}");

                Long categoryId = null;
                if (template.matches(uri)) {
                    Map<String, String> variables = template.match(uri);
                    categoryId =  Long.parseLong(variables.get("categoryId"));
                    message.put("category_id", categoryId.toString());
                }

                CategoryResponseDto categoryResponseDto = categoryService.getCategoryName(categoryId);
                String large = categoryResponseDto.getLargeCategory();
                String medium = categoryResponseDto.getMediumCategory();
                String small = categoryResponseDto.getSmallCategory();

                String category = Stream.of(large, medium, small)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(" > "));
                message.put("category_name",category);
            }
        }

        String jsonMessage = objectMapper.writeValueAsString(message);

        log.info(jsonMessage);
        return result;
    }

    private String getUsernameFromSecurity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "anonymous";
    }
}