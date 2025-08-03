package com.underrRndezvous.backend.controller;

import com.underrRndezvous.backend.controller.dto.MeetRequest;
import com.underrRndezvous.backend.controller.dto.response.MeetResponse;
import com.underrRndezvous.backend.controller.dto.response.PlaceDetailResponse;
import com.underrRndezvous.backend.service.MeetService;
import com.underrRndezvous.backend.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meet")
@RequiredArgsConstructor
public class MeetController {
    private final MeetService service;
    private final PlaceService placeService;

    @PostMapping
    public ResponseEntity<MeetResponse> createRecommendation(
            @RequestBody MeetRequest req) {
        return ResponseEntity.ok(service.recommend(req));
    }

    @GetMapping("/store/detail")
    public ResponseEntity<PlaceDetailResponse> getStoreDetail(
            @RequestParam("storeId") Long storeId) {
        PlaceDetailResponse detail = placeService.getPlaceDetail(storeId);
        return ResponseEntity.ok(detail);
    }
}