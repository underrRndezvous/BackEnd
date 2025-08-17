package com.underrRndezvous.backend.controller.dto.response;

import com.underrRndezvous.backend.domain.place.Place;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class PlaceRecommendation {
    private int order;
    private Long storeId;
    private String category;
    private String storeName;
    private Double placelati;
    private Double placelong;
    private boolean isOpen;
    private String address;
    private String subCategory;
}