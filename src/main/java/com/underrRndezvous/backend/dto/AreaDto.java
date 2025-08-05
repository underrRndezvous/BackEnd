package com.underrRndezvous.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AreaDto {
    @JsonProperty("hotplaceName")
    private String areaName;
    private String areaImage;
    private Double latitude;
    private Double longitude;

}