package com.underrRndezvous.backend.domain.user;

import com.underrRndezvous.backend.domain.meeting.Meeting;
import com.underrRndezvous.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "meeting_user")
public class MeetingUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_user_id")
    private Long meetingUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Builder
    private MeetingUser(User user, Meeting meeting, Location location) {
        this.user     = user;
        this.meeting  = meeting;
        this.location = location;
    }

    public static MeetingUser of(User user, Meeting meeting, Location location) {
        return MeetingUser.builder()
                .user(user)
                .meeting(meeting)
                .location(location)
                .build();
    }

}