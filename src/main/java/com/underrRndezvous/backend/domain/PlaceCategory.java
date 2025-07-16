package com.underrRndezvous.backend.domain;

import com.underrRndezvous.backend.domain.enums.PlaceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place_categories")
public class PlaceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private PlaceType name;

    @OneToMany(mappedBy = "category")
    private List<Place> places;

    @OneToMany(mappedBy = "placeCategory", cascade = CascadeType.ALL)
    private List<MeetingPlaceCategory> meetingPlaceCategories;

    @OneToMany(mappedBy = "placeCategory")
    private List<Mood> moods;
}
