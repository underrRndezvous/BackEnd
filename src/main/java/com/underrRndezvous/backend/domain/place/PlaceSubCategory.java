package com.underrRndezvous.backend.domain.place;

import com.underrRndezvous.backend.domain.enums.PlaceType;
import com.underrRndezvous.backend.domain.meeting.MeetingPlaceSubCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place_sub_categories")
public class PlaceSubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_sub_category_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "place_type", nullable = false)
    private PlaceType type;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL)
    private List<Place> places = new ArrayList<>();

    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL)
    private List<MeetingPlaceSubCategory> meetingPlaceSubCategories = new ArrayList<>();
}