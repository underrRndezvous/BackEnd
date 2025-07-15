package com.underrRndezvous.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity(name="users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "kakao_user_id", nullable = false,unique = true)
    private Long kakaoUserId;

    @Column(name="nickname",nullable = false)
    private String nickName;

    @Column(name="email", nullable = false)
    private String email;

    @Builder
    private User(Long kakaoUserId, String nickName, String email) {
        this.kakaoUserId = kakaoUserId;
        this.nickName = nickName;
        this.email = email;
    }

    public static User of(Long kakaoUserId, String nickName, String email) {
        return User.builder()
                .kakaoUserId(kakaoUserId)
                .nickName(nickName)
                .email(email)
                .build();
    }

}
