package com.underrRndezvous.backend.domain.place;

import com.underrRndezvous.backend.domain.meeting.MeetingArea;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "areas")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long id;

    @Column(name = "area_name", nullable = false)
    private String name;

    @Column(name = "latitude", nullable = false)
    private int latitude;

    @Column(name = "longitude", nullable = false)
    private int longitude;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
    private List<MeetingArea> meetingAreas = new ArrayList<>();

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
    private List<Place> places = new ArrayList<>();
}
