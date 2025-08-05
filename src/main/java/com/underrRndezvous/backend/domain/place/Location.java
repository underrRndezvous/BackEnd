package com.underrRndezvous.backend.domain.place;

import com.underrRndezvous.backend.domain.meeting.MeetingLocation;
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
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "si", nullable = false)
    private String si;

    @Column(name = "gu", nullable = false)
    private String gu;

    @Column(name = "dong", nullable = false)
    private String dong;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<MeetingLocation> meetingLocations = new ArrayList<>();

    @Builder
    public Location(Long locationId, String si, String gu, String dong,
                    Double latitude, Double longitude) {
        this.locationId = locationId;
        this.si         = si;
        this.gu         = gu;
        this.dong       = dong;
        this.latitude   = latitude;
        this.longitude  = longitude;
    }

    public static Location of(Long locationId, String si, String gu, String dong,
                              Double latitude, Double longitude) {
        return Location.builder()
                .locationId(locationId)
                .si(si)
                .gu(gu)
                .dong(dong)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}