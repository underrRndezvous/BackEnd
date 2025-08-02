package com.underrRndezvous.backend.controller;

import com.underrRndezvous.backend.controller.dto.MeetRequest;
import com.underrRndezvous.backend.controller.dto.response.MeetResponse;
import com.underrRndezvous.backend.service.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meet")
@RequiredArgsConstructor
public class MeetController {
    private final MeetService service;

    @PostMapping
    public ResponseEntity<MeetResponse> createRecommendation(
            @RequestBody MeetRequest req) {
        return ResponseEntity.ok(service.recommend(req));
    }
}