package com.underrRndezvous.backend.domain.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.underrRndezvous.backend.domain.meeting.MeetingArea;
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
@Table(name = "areas")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "area_name", nullable = false)
    // @JsonProperty("hotplaceName")
    private String areaName;

    @Column(name = "area_image")
    private String areaImage;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingArea> meetingAreas = new ArrayList<>();

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Place> places = new ArrayList<>();

    @Builder
    public Area(Long areaId, String areaName, String areaImage, Double latitude, Double longitude) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.areaImage = areaImage;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Area of(Long areaId, String areaName, String areaImage, Double latitude, Double longitude) {
        return Area.builder()
                .areaId(areaId)
                .areaName(areaName)
                .areaImage(areaImage)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
