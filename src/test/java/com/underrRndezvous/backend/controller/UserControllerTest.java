package com.underrRndezvous.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.underrRndezvous.backend.client.kakao.KakaoUserInfoResponse;
import com.underrRndezvous.backend.dto.KakaoLoginRequest;
import com.underrRndezvous.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    ObjectMapper objectMapper = new ObjectMapper();

    MockHttpSession httpSession;

    @MockBean
    UserService userService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("만약 카카오 로그인이 성공하는 경우 카카오 로그인이 성공해야된다.")
    void kakaoLoginAPITest() throws Exception {
        KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest();
        KakaoUserInfoResponse kakaoUserInfoResponse = new KakaoUserInfoResponse(1L,"asdasdasdasd","asdasdasd@naver.com");

        mockMvc.perform(post("/oauth/kakao/callback"))
                .andExpect(status().isOk());

    }

}
