package com.underrRndezvous.backend.controller;

import com.underrRndezvous.backend.dto.KakaoLoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class TestController {

    @PostMapping("/oauth/kakao/callback")
    public void kakaoLoginTest(@RequestBody KakaoLoginRequest kakaoLoginRequest) {
        log.info("kakaoLoginRequest={}", kakaoLoginRequest.getCode());
    }

}

