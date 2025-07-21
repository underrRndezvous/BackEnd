package com.underrRndezvous.backend.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AreaResponse {
    private Long areaId;
    private Double latitude;
    private Double longitude;
    private Double distance;    // 중간 위치로부터의 거리 (km)

    @JsonProperty("hotplaceName")
    private String areaName;

    public AreaResponse(Long areaId, String areaName,
                        Double latitude, Double longitude,
                        Double distance) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }
}