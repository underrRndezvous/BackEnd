package com.underrRndezvous.backend.controller;


import com.underrRndezvous.backend.dto.KakaoLoginRequest;
import com.underrRndezvous.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/oauth/kakao/callback")
    public void kakaoLoginProcess(@RequestBody KakaoLoginRequest kakaoLoginRequest, HttpSession session) {
        userService.kakaoLoginProcess(kakaoLoginRequest);
    }

}
