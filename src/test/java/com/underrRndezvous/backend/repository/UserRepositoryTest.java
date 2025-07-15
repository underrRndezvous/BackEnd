package com.underrRndezvous.backend.repository;

import com.underrRndezvous.backend.domain.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    EntityManager em;

    User user;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        user = User.of(1234L, "test1234", "test@test.com");
        em.persist(user);
        em.flush();
    }


    @Test
    @DisplayName("만약 유저가 존재하는 경우 kakaoUserId로 조회 시에 성공하여야 한다.")
    public void findUserByKakaoUserIdTest() {
        Optional<User> findUser = userRepository.findUserByKakaoUserId(1234L);

        assertThat(findUser.get().getKakaoUserId()).isEqualTo(user.getKakaoUserId());
    }

    @Test
    @DisplayName("만약 조회하고자 하는 유저가 없는 경우 Null을 출력해야 한다.")
    public void findUserByKakaoUserIdNullTest() {
        Optional<User> findUser = userRepository.findUserByKakaoUserId(9999L);
        assertThat(findUser).isEmpty();
    }

}
