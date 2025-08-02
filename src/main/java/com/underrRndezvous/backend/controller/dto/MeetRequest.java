package com.underrRndezvous.backend.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MeetRequest {
    private String groupName;
    private String meetTime;
    private List<PlaceRequest> place;
    private List<StartPoint> startPoint;
}