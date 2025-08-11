package com.underrRndezvous.backend.domain.meeting;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "meeting_place_sub_categories")
public class MeetingPlaceSubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_place_sub_category_id")
    private Long id;

    @Column(name = "place_count", nullable = false)
    private int placeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;


}
