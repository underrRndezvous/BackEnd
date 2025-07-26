package com.underrRndezvous.backend.controller;

import com.underrRndezvous.backend.controller.dto.response.AreaResponse;
import com.underrRndezvous.backend.service.AreaRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meet")
@RequiredArgsConstructor
@Slf4j
public class AreaRecommendationController {

    private final AreaRecommendationService recommendationService;

    /**
     * GET /meet/recommend/{meetingId}?limit=3
     */
    @GetMapping("/recommend/{meetingId}")
    public List<AreaResponse> recommendAreas(
            @PathVariable Long meetingId,
            @RequestParam(defaultValue = "3")
            int limit
    )
    {
        log.info("recommendAreas called for meetingId={}, limit={}", meetingId, limit);
        return recommendationService.recommendByMeeting(meetingId, limit);
    }
}
