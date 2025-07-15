package com.underrRndezvous.backend.service;

import com.underrRndezvous.backend.client.kakao.KakaoClient;
import com.underrRndezvous.backend.client.kakao.KakaoUserInfoResponse;
import com.underrRndezvous.backend.controller.dto.KakaoLoginRequest;
import com.underrRndezvous.backend.domain.User;
import com.underrRndezvous.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@SpringBootTest
@Transactional
public class UserServiceTest {

    @MockBean
    KakaoClient kakaoClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @BeforeEach
    public void setUp() {

    }

    @Test
    @DisplayName("만약 카카오 로그인이 성공한 경우 user id를 반환해야하며, 해당 Id는 데이터베이스에서 조회가 가능하다.")
    public void kakaoUserIdTest() {
        KakaoUserInfoResponse kakaoUserInfoResponse = new KakaoUserInfoResponse(3333L, "nickname", "test@naver.com");
        KakaoLoginRequest accessToken = new KakaoLoginRequest("accessToken");
        given(kakaoClient.getAccessToken(any(String.class))).willReturn("asdasdasdasd");
        given(kakaoClient.getUserInfo(any(String.class))).willReturn(kakaoUserInfoResponse);
        Long result = userService.kakaoLoginProcess(accessToken);
        System.out.println("kakaoUserInfoResponsetest:"+kakaoUserInfoResponse);
        Optional<User> findUser = userRepository.findById(result);
        assertThat(findUser).isPresent();
        assertThat(result).isEqualTo(1L);
    }


    @Test
    @DisplayName("만약 카카오 로그인에 실패한 경우 RuntimeException을 발생시켜야 한다.")
    public void kakaoUserIdExceptionTest() {
        given(kakaoClient.getAccessToken(any(String.class))).willReturn("asdasdasdasd");
        given(kakaoClient.getUserInfo(any(String.class))).willThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> userService.kakaoLoginProcess(new KakaoLoginRequest("accessToken")));
    }


}
