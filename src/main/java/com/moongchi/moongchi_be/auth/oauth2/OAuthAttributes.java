package com.moongchi.moongchi_be.auth.oauth2;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;
    private String provider;
    private String providerId;

    @Builder
    public OAuthAttributes(String nameAttributeKey, String name, String email, String picture, String provider, String providerId) {
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static OAuthAttributes of(String provider, Map<String, Object> attributes) {
        return switch (provider.toLowerCase()) {
            case "google" -> ofGoogle(attributes);
            case "naver" -> ofNaver(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

    private static OAuthAttributes ofGoogle(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .provider("google")
                .providerId((String) attributes.get("sub"))
                .nameAttributeKey("sub")
                .build();
    }

    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .provider("naver")
                .providerId((String) response.get("id"))
                .nameAttributeKey("id")
                .build();
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) profile.get("profile_image_url"))
                .provider("kakao")
                .providerId(String.valueOf(attributes.get("id")))
                .nameAttributeKey("id")
                .build();
    }
}