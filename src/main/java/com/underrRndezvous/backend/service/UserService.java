package com.underrRndezvous.backend.service;

import com.underrRndezvous.backend.client.kakao.KakaoClient;
import com.underrRndezvous.backend.client.kakao.KakaoOAuthTokenRequest;
import com.underrRndezvous.backend.client.kakao.KakaoUserInfoResponse;
import com.underrRndezvous.backend.dto.KakaoLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final KakaoClient kakaoClient;

    public void kakaoLoginProcess(KakaoLoginRequest kakaoLoginRequest) {
        KakaoUserInfoResponse userInfo = kakaoClient.getUserInfo(kakaoClient.getAccessToken(kakaoLoginRequest.getCode()));

        log.info("userInfo: {}", userInfo);
    }

}
