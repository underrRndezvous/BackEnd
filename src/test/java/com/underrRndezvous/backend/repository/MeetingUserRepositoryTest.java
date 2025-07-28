package com.underrRndezvous.backend.repository;

import com.underrRndezvous.backend.domain.enums.MeetingType;
import com.underrRndezvous.backend.domain.enums.TimeType;
import com.underrRndezvous.backend.domain.meeting.Meeting;
import com.underrRndezvous.backend.domain.user.Location;
import com.underrRndezvous.backend.domain.user.MeetingUser;
import com.underrRndezvous.backend.domain.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MeetingUserRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MeetingUserRepository meetingUserRepository;

    private User user;
    private Meeting meeting;
    private Location location;

    @BeforeEach
    void setUp() {
        // User, Location, Meeting 엔티티를 영속화
        user = User.of(1234L,"name", "email@example.com");
        location = new Location(null, "서울특별시", "종로구", "혜화동", 37.587817, 127.001745);
        meeting = Meeting.of("테스트 모임", MeetingType.FRIENDS, TimeType.MORNING);

        em.persist(user);
        em.persist(location);
        em.persist(meeting);
        em.flush();
    }

    @Test
    @DisplayName("존재하지 않는 meetingId 조회 시 빈 리스트를 반환한다")
    void findByMeetingMeetingId_Empty() {
        List<MeetingUser> empty = meetingUserRepository.findByMeetingMeetingId(9999L);
        assertThat(empty).isEmpty();
    }

    @Test
    @DisplayName("meetingId로 조회 시 해당 MeetingUser 리스트를 반환한다")
    void findByMeetingMeetingId_Success() throws Exception {
        MeetingUser mu = MeetingUser.of(user, meeting, location);

        // 영속화
        em.persist(mu);
        em.flush();

        // 4) 검증
        List<MeetingUser> result = meetingUserRepository.findByMeetingMeetingId(meeting.getMeetingId());
        assertThat(result).hasSize(1);

        MeetingUser found = result.get(0);
        assertThat(found.getUser().getKakaoUserId()).isEqualTo(1234L);
        assertThat(found.getMeeting().getMeetingId()).isEqualTo(meeting.getMeetingId());
    }
}
