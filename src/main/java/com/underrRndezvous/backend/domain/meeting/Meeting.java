package com.underrRndezvous.backend.domain.meeting;

import com.underrRndezvous.backend.domain.common.BaseEntity;
import com.underrRndezvous.backend.domain.enums.MeetingType;
import com.underrRndezvous.backend.domain.enums.TimeType;
import com.underrRndezvous.backend.domain.user.MeetingUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "meetings")
public class Meeting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long meetingId;

    @Column(name = "meeting_name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_category")
    private MeetingType category;

    @Column(name = "meeting_time", nullable = false)
    private TimeType meetingTime;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<MeetingUser> meetingUsers = new ArrayList<>();

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<MeetingArea> meetingAreas = new ArrayList<>();

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<MeetingPlaceCategory> placeCategories = new ArrayList<>();

    @Builder
    public Meeting(String name, MeetingType category, TimeType meetingTime) {
        this.name        = name;
        this.category    = category;
        this.meetingTime = meetingTime;
    }

    public static Meeting of(String name, MeetingType category, TimeType meetingTime) {
        return Meeting.builder()
                .name(name)
                .category(category)
                .meetingTime(meetingTime)
                .build();
    }


}