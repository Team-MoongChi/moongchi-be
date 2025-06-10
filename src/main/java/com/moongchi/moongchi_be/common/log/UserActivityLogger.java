package com.moongchi.moongchi_be.common.log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivityLogger {

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
            Object rawBody = ((ResponseEntity<?>) result).getBody();
            Map<String, Object> body = objectMapper.convertValue(rawBody, new TypeReference<>() {});


            message.put("item_id", body.get("id"));
            message.put("item_name",body.get("name"));
            message.put("clicked_at", Instant.now().toString());
            String category = body.get("largeCategory") + " > " + body.get("mediumCategory") + " > " + body.get("smallCategory");
            message.put("item_category_id", body.get("categoryId"));
            message.put("item_category", category);
            message.put("item_price", body.get("price"));
//            message.put("review_count",0);
//            message.put("rank_in_list", "");
//            message.put("page_type","");
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