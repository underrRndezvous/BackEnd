package com.underrRndezvous.backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RegionRecommendation {
    private String hotPlace;
    private String hotPlaceImage;
    private List<PlaceRecommendation> recommendPlace;
}