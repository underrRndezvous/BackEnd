package com.underrRndezvous.backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetResponse {
    private RegionRecommendation first;
    private RegionRecommendation second;
    private RegionRecommendation third;
}
