package com.underrRndezvous.backend.domain.place;

import com.underrRndezvous.backend.domain.enums.PlaceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "places")
public class Place {

    @Id
    @Column(name = "place_id")
    private Long id;

    @Column(name = "place_name", nullable = false)
    private String name;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "atmosphere")
    private String atmosphere;


    @Convert(converter = BusinessHoursConverter.class)
    @Column(name = "business_hours", columnDefinition = "JSON")
    private BusinessHours businessHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "place_type", nullable = false)
    private PlaceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @Column(name = "sub_category_name")
    private String subCategoryName;

    @Column(name = "address")
    private String address;
    
    public boolean isOpenAt(LocalDateTime dateTime) {
        return businessHours != null && businessHours.isOpenAt(dateTime);
    }
    
    public boolean isOpenNow() {
        return isOpenAt(LocalDateTime.now());

    }
    public boolean isOpenDuring(LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek) {
        return businessHours != null && businessHours.isOpenDuring(startTime, endTime, dayOfWeek);
    }
    
    public DaySchedule getTodaySchedule() {
        return businessHours != null ? businessHours.getScheduleForDay(LocalDateTime.now().getDayOfWeek()) : null;
    }

    @Builder
    public Place(Long id, String name, Integer reviewCount, Double latitude, Double longitude, 
                 String atmosphere, BusinessHours businessHours, PlaceType type, 
                 Area area, String subCategoryName, String address) {
        this.id = id;
        this.name = name;
        this.reviewCount = reviewCount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.atmosphere = atmosphere;
        this.businessHours = businessHours;
        this.type = type;
        this.area = area;
        this.subCategoryName = subCategoryName;
        this.address = address;
    }

    public static Place of(Long id, String name, Integer reviewCount, Double latitude, Double longitude,
                          String atmosphere, BusinessHours businessHours, PlaceType type,
                          Area area, String subCategoryName, String address) {
        return Place.builder()
                .id(id)
                .name(name)
                .reviewCount(reviewCount)
                .latitude(latitude)
                .longitude(longitude)
                .atmosphere(atmosphere)
                .businessHours(businessHours)
                .type(type)
                .area(area)
                .subCategoryName(subCategoryName)
                .address(address)
                .build();
    }
}