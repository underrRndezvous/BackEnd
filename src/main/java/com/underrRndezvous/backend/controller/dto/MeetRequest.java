package com.underrRndezvous.backend.controller.dto;

import com.underrRndezvous.backend.domain.enums.DayType;
import com.underrRndezvous.backend.domain.enums.TimeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MeetRequest {
    private String groupName;
    private List<TimeType> meetTime;
    private DayType meetDays;
    private List<PlaceRequest> place;
    private List<StartPoint> startPoint;
}