package com.moongchi.moongchi_be.common.config;

import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenApiCustomizer sortTagsCustomizer() {
        return openApi -> {
            openApi.setTags(List.of(
                    new Tag().name("유저").description("유저관련 API"),
                    new Tag().name("상품").description("상품관련 API"),
                    new Tag().name("공구상품").description("공구상품관련 API"),
                    new Tag().name("관심상품").description("관심상품관련 API"),
                    new Tag().name("채팅").description("채팅관련 API"),
                    new Tag().name("카테고리").description("카테고리관련 API")

            ));
        };
    }
}
