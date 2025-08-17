package com.underrRndezvous.backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceDetailResponse {
    private Long storeId;
    private String storeType;
    private String storeDetail;
    private String storeName;
    private Double rating;
    private Integer reviewCount;
    private String address;
    private String businessHours;
    private String image;
    private String subCategory;
}
