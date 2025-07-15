package com.underrRndezvous.backend.controller;


import com.underrRndezvous.backend.config.ConstValue;
import com.underrRndezvous.backend.controller.dto.KakaoLoginRequest;
import com.underrRndezvous.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/oauth/kakao/callback")
    public void kakaoLoginProcess(@RequestBody KakaoLoginRequest kakaoLoginRequest, HttpSession session) {
        Long userId = userService.kakaoLoginProcess(kakaoLoginRequest);
        setSession(session, userId);
    }

    private void setSession(HttpSession session, Long userId) {
        session.setAttribute(ConstValue.sessionName, userId);
        session.setMaxInactiveInterval(1800);
    }

}
