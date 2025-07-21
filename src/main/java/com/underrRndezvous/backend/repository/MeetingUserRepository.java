package com.underrRndezvous.backend.repository;

import com.underrRndezvous.backend.domain.user.MeetingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingUserRepository extends JpaRepository<MeetingUser, Long> {
    List<MeetingUser> findByMeetingMeetingId(Long meetingId);
}
