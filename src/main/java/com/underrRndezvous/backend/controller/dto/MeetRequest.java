package com.underrRndezvous.backend.controller.dto;

import com.underrRndezvous.backend.domain.enums.DayType;
import com.underrRndezvous.backend.domain.enums.TimeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MeetRequest {
    private String groupName;
    private TimeType meetTime;
    private DayType meetDays;
    private List<PlaceRequest> place;
    private List<StartPoint> startPoint;
}