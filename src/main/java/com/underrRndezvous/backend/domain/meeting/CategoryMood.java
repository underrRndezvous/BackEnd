package com.underrRndezvous.backend.domain.meeting;

import com.underrRndezvous.backend.domain.place.Mood;
import com.underrRndezvous.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category_moods")
public class CategoryMood extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_mood_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_id", nullable = false)
    private Mood mood;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_place_category_id", nullable = false)
    private MeetingPlaceCategory meetingPlaceCategory;

    @Column(name = "auto_assigned", nullable = false)
    private Boolean autoAssigned = true;
}
