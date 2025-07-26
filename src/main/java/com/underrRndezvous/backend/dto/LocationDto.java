package com.underrRndezvous.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationDto {
    @JsonProperty("sido")
    private String si;

    private String gu;
    private String dong;

    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lng")
    private double lng;

}