package com.underrRndezvous.backend.kakao;

import com.underrRndezvous.backend.client.kakao.KakaoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.assertj.core.api.Assertions.assertThat;

@RestClientTest(KakaoClient.class)
public class KakaoClientTest {

    @Autowired
    private KakaoClient kakaoClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
            return builder.build();
        }
    }

    @Test
    @DisplayName("카카오 로그인 성공 테스트")
    void kakaoLoginProcessSuccessTest() {
        mockServer.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andRespond(
                        withSuccess("{\"access_token\":\"asdasdasdasdasdas\", \"refresh_token\":\"asdasdasdasd\" }", MediaType.APPLICATION_JSON)
                );

        String accessToken = kakaoClient.getAccessToken("asdasdasd");
        assertThat(accessToken).isEqualTo("asdasdasdasdasdas");
    }


}
