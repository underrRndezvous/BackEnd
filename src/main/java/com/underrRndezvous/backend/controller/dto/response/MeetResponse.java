package com.underrRndezvous.backend.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.List;

@Getter
public class MeetResponse {
    @JsonProperty("meetingId")
    private Long meetingId;
    
    @JsonProperty("regions")
    private List<RegionRecommendation> regions;
    
    public MeetResponse(List<RegionRecommendation> regions) {
        this.regions = regions;
        this.meetingId = null;
    }
    
    public MeetResponse(Long meetingId, List<RegionRecommendation> regions) {
        this.meetingId = meetingId;
        this.regions = regions;
    }
}
