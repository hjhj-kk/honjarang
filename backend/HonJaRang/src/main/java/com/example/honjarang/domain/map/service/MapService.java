package com.example.honjarang.domain.map.service;

import com.example.honjarang.domain.map.dto.CoordinateDto;
import com.example.honjarang.domain.map.exception.PlaceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
public class MapService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    public CoordinateDto getCoordinate(String keyword) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        URI targetUrl = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("query", keyword)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        ResponseEntity<String> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
            if(jsonNode.get("documents").size() == 0) {
                throw new PlaceNotFoundException("장소를 찾을 수 없습니다.");
            }
            Double latitude = jsonNode.get("documents").get(0).get("y").asDouble();
            Double longitude = jsonNode.get("documents").get(0).get("x").asDouble();
            return new CoordinateDto(latitude, longitude);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getDistance(CoordinateDto startCoordinateDto, CoordinateDto endCoordinateDto) {
        // Haversine 공식을 사용하여 좌표를 계산해서 m 단위로 변환
        double startLatRad = Math.toRadians(startCoordinateDto.getLatitude());
        double startLonRad = Math.toRadians(startCoordinateDto.getLongitude());
        double endLatRad = Math.toRadians(endCoordinateDto.getLatitude());
        double endLonRad = Math.toRadians(endCoordinateDto.getLongitude());

        double distance = 6371 * Math.acos(Math.cos(startLatRad) * Math.cos(endLatRad) * Math.cos(startLonRad - endLonRad) + Math.sin(startLatRad) * Math.sin(endLatRad));
        return (int) (distance * 1000);
    }
}
