package com.underrRndezvous.backend.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = IDENTITY)
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
    private List<MeetingUser> meetingUsers = new ArrayList<>();

    public Location(Long locationId, String si, String gu, String dong,
                    Double latitude, Double longitude) {
        this.locationId = locationId;
        this.si         = si;
        this.gu         = gu;
        this.dong       = dong;
        this.latitude   = latitude;
        this.longitude  = longitude;
    }
}