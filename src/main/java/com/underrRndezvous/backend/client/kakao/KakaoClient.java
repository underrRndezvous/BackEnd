package com.underrRndezvous.backend.client.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class KakaoClient {

    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    // 1) 인가 코드로 액세스 토큰 발급
    public String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("grant_type",   "authorization_code");
        params.add("client_id",    clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code",         code);

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params, headers);
        KakaoOAuthTokenRequest response = restTemplate.postForObject(tokenUrl, request, KakaoOAuthTokenRequest.class);

        return response.getAccessToken();
    }

    // 2) 액세스 토큰으로 유저 정보 조회
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        String userUrl = "https://kapi.kakao.com/v2/user/me";

        // 헤더에 Bearer 토큰 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // ParameterizedTypeReference 로 제네릭 정보 보존
        ResponseEntity<Map<String, Object>> responseEntity =
                restTemplate.exchange(
                        userUrl,
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<Map<String, Object>>() {}
                );

        Map<String, Object> response = responseEntity.getBody();

        // 필요한 정보 꺼내기
        Long id = ((Number) response.get("id")).longValue();
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) response.get("properties");
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");

        // DTO에 담아서 반환
        KakaoUserInfoResponse info = new KakaoUserInfoResponse();
        info.setId(id);
        info.setNickname((String) properties.get("nickname"));
        info.setEmail((String) kakaoAccount.get("email"));

        return info;
    }

    // 통합 메서드
    public KakaoUserInfoResponse processKakaoLogin(String code) {
        String token = getAccessToken(code);
        return getUserInfo(token);
    }
}
