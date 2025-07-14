package com.underrRndezvous.backend.service;

import com.underrRndezvous.backend.client.kakao.KakaoClient;
import com.underrRndezvous.backend.client.kakao.KakaoUserInfoResponse;
import com.underrRndezvous.backend.domain.User;
import com.underrRndezvous.backend.dto.KakaoLoginRequest;
import com.underrRndezvous.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class UserService {

    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;

    @Transactional
    public Long kakaoLoginProcess(KakaoLoginRequest kakaoLoginRequest) {
        KakaoUserInfoResponse userInfo = kakaoClient.getUserInfo(kakaoClient.getAccessToken(kakaoLoginRequest.getCode()));

        Optional<User> findUser = userRepository.findUserByKakaoUserId(userInfo.getId());

        log.info("findUser={}", findUser.orElse(null));

        if(findUser.isEmpty()) {
            User user = User.of(userInfo.getId(), userInfo.getNickname(), userInfo.getNickname());
            user = userRepository.save(user);

            return user.getId();
        }

        return findUser.get().getId();
    }

}
