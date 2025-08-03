package com.underrRndezvous.backend.service;

import com.underrRndezvous.backend.controller.dto.MeetRequest;
import com.underrRndezvous.backend.controller.dto.response.MeetResponse;
import com.underrRndezvous.backend.controller.dto.response.PlaceRecommendation;
import com.underrRndezvous.backend.controller.dto.response.RegionRecommendation;
import com.underrRndezvous.backend.domain.enums.PlaceType;
import com.underrRndezvous.backend.domain.place.Area;
import com.underrRndezvous.backend.domain.place.Place;
import com.underrRndezvous.backend.repository.AreaRepository;
import com.underrRndezvous.backend.repository.PlaceRepository;
import com.underrRndezvous.backend.util.DistanceCalculator;
import com.underrRndezvous.backend.dto.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetService {
    private final PlaceService placeService;
    private final PlaceRepository placeRepo;
    private final AreaRepository areaRepo;

    public MeetResponse recommend(MeetRequest req) {
        // 1) 참여자 출발지 -> 위도, 경도 좌표
        List<Position> positions = req.getStartPoint().stream()
                .map(sp -> placeService.getPositionFromArea(sp.getFirst(), sp.getSecond(), sp.getThird()))
                .collect(Collectors.toList());

        // 2) 지구 구면 중간 위치 계산
        double[] midpoint = DistanceCalculator.calculateGeographicMidpoint(positions);
        double midLat = midpoint[0];
        double midLng = midpoint[1];

        // 3) 모든 Area 조회 -> 거리 오름차순 정렬 -> 상위 3개
        List<Area> nearestAreas = areaRepo.findAll().stream()
                .sorted(Comparator.comparingDouble(a ->
                        DistanceCalculator.distance(midLat, midLng, a.getLatitude(), a.getLongitude())
                ))
                .limit(3)
                .toList();

        // 4) 각 지역별로 요청된 서브카테고리별 가게 추천
        List<RegionRecommendation> regions = nearestAreas.stream()
                .map(area -> {
                    String hotPlace = area.getAreaName();
                    String imageUrl = "https://ncloud.s3.com/" + hotPlace + ".jpg";

                    List<PlaceRecommendation> recs = req.getPlace().stream()
                            .map(pr -> {
                                PlaceType type = pr.getPlaceType().equalsIgnoreCase("drinking")
                                        ? PlaceType.BAR
                                        : PlaceType.valueOf(pr.getPlaceType().toUpperCase());
                                // 요청된 서브카테고리별 후보 리스트 조회
                                List<Place> candidates = placeRepo.findByTypeAndSubCategory_Name(
                                        type, pr.getTypeDetail()
                                );
                                return candidates.stream()
                                        .min(Comparator.comparingDouble(p ->
                                                DistanceCalculator.distance(
                                                        midLat, midLng, p.getLatitude(), p.getLongitude()
                                                )
                                        ))
                                        .map(p -> new PlaceRecommendation(
                                                pr.getId(),
                                                p.getId(),
                                                p.getName(),
                                                p.getLatitude(),
                                                p.getLongitude()
                                        ))
                                        .orElse(null);
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    return new RegionRecommendation(hotPlace, imageUrl, recs);
                })
                .toList();

        return new MeetResponse(
                regions.get(0),
                regions.get(1),
                regions.get(2)
        );
    }
}
