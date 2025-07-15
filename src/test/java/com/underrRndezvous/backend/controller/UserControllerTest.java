package com.underrRndezvous.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.underrRndezvous.backend.client.kakao.KakaoUserInfoResponse;
import com.underrRndezvous.backend.config.ConstValue;
import com.underrRndezvous.backend.controller.dto.KakaoLoginRequest;
import com.underrRndezvous.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
        httpSession = new MockHttpSession();
    }

    @Test
    @DisplayName("만약 카카오 로그인이 성공하는 경우 카카오 로그인이 성공해야된다.")
    void kakaoLoginAPITest() throws Exception {
        KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest("asdasdasdas");

        given(userService.kakaoLoginProcess(any(KakaoLoginRequest.class))).willReturn(1L);

        MvcResult mvcResult = mockMvc.perform(post("/user/oauth/kakao/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kakaoLoginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String sessionValue = mvcResult.getRequest().getSession().getAttribute(ConstValue.sessionName).toString();
        assertThat(sessionValue).isNotEmpty();
    }

}
