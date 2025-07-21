package com.underrRndezvous.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AreaDto {
    @JsonProperty("hotplaceName")
    private String areaName;
    private Double latitude;
    private Double longitude;

}