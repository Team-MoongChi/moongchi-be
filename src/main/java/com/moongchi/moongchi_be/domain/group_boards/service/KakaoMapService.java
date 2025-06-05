package com.moongchi.moongchi_be.domain.group_boards.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Data;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;





@Service
public class KakaoMapService {
    private final String kakaoClientId;

    public KakaoMapService() {
        Dotenv dotenv = Dotenv.load();
        this.kakaoClientId = dotenv.get("KAKAO_CLIENT_ID");
    }


    public Coordinate getCoordinateFromAddress(String address) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;


        HttpHeaders headers = new HttpHeaders();
        String authHeader = "KakaoAK " + kakaoClientId;
        headers.set("Authorization", authHeader);
        headers.set("content-type", "application/json;charset=UTF-8");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<KakaoAddressResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                KakaoAddressResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            if (!response.getBody().getDocuments().isEmpty()) {
                KakaoAddressDocument doc = response.getBody().getDocuments().get(0);
                return new Coordinate(Double.parseDouble(doc.getY()), Double.parseDouble(doc.getX()));
            }
        }

        throw new RuntimeException("좌표 변환 실패");
    }
}

@Data
class KakaoAddressResponse {
    private java.util.List<KakaoAddressDocument> documents;
}

@Data
class KakaoAddressDocument {
    private String address_name;
    private String x;
    private String y;
}

@Data
class Coordinate {
    private double latitude;
    private double longitude;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}