package com.underrRndezvous.backend.repository;


import com.underrRndezvous.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("select u from users u where u.kakaoUserId = :kakaoUserId")
    Optional<User> findUserByKakaoUserId(@Param(value="kakaoUserId") Long kakaoUserId);

}
