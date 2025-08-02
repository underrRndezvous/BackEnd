package com.underrRndezvous.backend.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceRequest {
    private int id;
    private String placeType;
    private String typeDetail;
}