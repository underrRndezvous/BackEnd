package com.underrRndezvous.backend.domain.meeting;

import com.underrRndezvous.backend.domain.common.BaseEntity;
import com.underrRndezvous.backend.domain.enums.MeetingType;
import com.underrRndezvous.backend.domain.enums.TimeType;
import com.underrRndezvous.backend.domain.user.MeetingUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "meetings")
public class Meeting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;

    @Column(name = "meeting_name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_category")
    private MeetingType category;

    @Column(name = "meeting_time", nullable = false)
    private TimeType meetingTime;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<MeetingUser> meetingUsers;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<MeetingArea> meetingAreas;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<MeetingPlaceCategory> placeCategories;

}