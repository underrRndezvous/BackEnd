
package com.underrRndezvous.backend.controller;

import com.underrRndezvous.backend.dto.KakaoLoginRequest;
import com.underrRndezvous.backend.dto.KakaoUserInfo;
import com.underrRndezvous.backend.service.KakaoService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;


@RestController
@RequestMapping("/oauth/kakao")
@Slf4j
public class TestController {
    private final KakaoService kakaoService;

    public TestController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @PostMapping("/callback")
    public ResponseEntity<Object> kakaoLoginTest(@RequestBody KakaoLoginRequest kakaoLoginRequest, HttpSession session) {
        try {
            // 1) 카카오에서 유저 정보 가져오기
            KakaoUserInfo kakaoUser = kakaoService.processKakaoLogin(kakaoLoginRequest.getCode());
            log.info("kakaoUser={}", kakaoUser);

            // 2) 세션에 KakaoUserInfo 저장
            session.setAttribute("LOGIN_USER_ID", kakaoUser.getId());
            session.setAttribute("LOGIN_USER_NICKNAME", kakaoUser.getNickname());

            // 3) 프론트에 바로 DTO 반환
            return ResponseEntity.ok(kakaoUser);

        } catch (HttpClientErrorException.BadRequest ex) {
            log.error("인가 코드 오류", ex);
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "인가 코드가 유효하지 않습니다."));
        } catch (Exception ex) {
            log.error("카카오 로그인 처리 중 예외 발생", ex);
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "서버 내부 오류가 발생했습니다."));

        }
    }

}
