package com.underrRndezvous.backend.service;

import com.underrRndezvous.backend.controller.dto.MeetRequest;
import com.underrRndezvous.backend.controller.dto.PlaceRequest;
import com.underrRndezvous.backend.controller.dto.response.MeetResponse;
import com.underrRndezvous.backend.controller.dto.response.PlaceRecommendation;
import com.underrRndezvous.backend.controller.dto.response.RegionRecommendation;
import com.underrRndezvous.backend.domain.enums.AreaGroup;
import com.underrRndezvous.backend.domain.enums.PlaceType;
import com.underrRndezvous.backend.domain.enums.CafeAtmosphere;
import com.underrRndezvous.backend.domain.place.Area;
import com.underrRndezvous.backend.domain.place.Place;
import com.underrRndezvous.backend.repository.AreaRepository;
import com.underrRndezvous.backend.repository.PlaceRepository;
import com.underrRndezvous.backend.util.DistanceCalculator;
import com.underrRndezvous.backend.dto.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

        // 3) 그룹별 가장 가까운 대표 지역 조회 -> 상위 1개 그룹의 모든 지역 선택
        List<Area> nearestAreas = findNearestAreasByGroup(midLat, midLng);

        // 4) 각 지역별로 요청된 서브카테고리별 가게 추천
        List<RegionRecommendation> regions = nearestAreas.stream()
                .map(area -> createRegionRecommendation(area, req))
                .toList();

        return new MeetResponse(regions);
    }

    private RegionRecommendation createRegionRecommendation(Area area, MeetRequest req) {
        String hotPlace = area.getAreaName();
        String imageUrl = "https://ncloud.s3.com/" + hotPlace + ".jpg";
        
        // 모임 시간 파싱
        LocalTime meetTime = parseMeetTime(req.getMeetTime());
        DayOfWeek meetDay = parseMeetDay(req.getMeetDays());
        
        List<PlaceRecommendation> placeRecommendations = req.getPlace().stream()
                .flatMap(placeRequest -> findRandomPlaces(placeRequest, area, meetTime, meetDay).stream())
                .sorted(Comparator.comparingInt(PlaceRecommendation::getOrder))
                .collect(Collectors.toList());
        
        return new RegionRecommendation(hotPlace, imageUrl, placeRecommendations);
    }

    private List<PlaceRecommendation> findRandomPlaces(PlaceRequest placeRequest, Area area, LocalTime meetTime, DayOfWeek meetDay) {
        PlaceType type = PlaceType.valueOf(placeRequest.getPlaceType().toUpperCase());
        String detail = placeRequest.getTypeDetail();
        
        List<Place> candidates;
        
        // 카페인 경우 분위기 필터링 적용
        if (type == PlaceType.CAFE && placeRequest.getCafeAtmosphere() != null) {
            CafeAtmosphere atmosphere = CafeAtmosphere.valueOf(placeRequest.getCafeAtmosphere().toUpperCase());
            candidates = placeRepo.findByAreaAreaIdAndTypeAndSubCategoryNameAndCafeAtmosphere(area.getAreaId(), type, detail, atmosphere);
        } else {
            candidates = placeRepo.findByAreaAreaIdAndTypeAndSubCategoryName(area.getAreaId(), type, detail);
        }
        
        // 영업시간 필터링 적용
        List<Place> filteredCandidates = candidates.stream()
                .filter(place -> isOpenDuringMeetTime(place, meetTime, meetDay))
                .collect(Collectors.toList());
        
        // 랜덤 순서로 섞기
        List<Place> shuffledCandidates = new ArrayList<>(filteredCandidates);
        Collections.shuffle(shuffledCandidates);
        
        // 최대 1개만 선택
        return shuffledCandidates.stream()
                .limit(1)
                .map(place -> new PlaceRecommendation(
                        placeRequest.getId(),
                        place.getId(),
                        place.getSubCategory().getName(),
                        place.getName(),
                        place.getLatitude(),
                        place.getLongitude()
                ))
                .collect(Collectors.toList());
    }

    private List<Area> findNearestAreasByGroup(double midLat, double midLng) {
        List<Area> allAreas = areaRepo.findAll();
        
        List<List<String>> areaGroups = AreaGroup.getAllAreaGroups();
        
        List<String> nearestGroupNames = areaGroups.stream()
                .min(Comparator.comparingDouble(groupNames -> {
                    Area nearestAreaInGroup = findNearestAreaInGroup(groupNames, allAreas, midLat, midLng);
                    return nearestAreaInGroup != null ? 
                        DistanceCalculator.distance(midLat, midLng, nearestAreaInGroup.getLatitude(), nearestAreaInGroup.getLongitude()) :
                        Double.MAX_VALUE;
                }))
                .orElse(Collections.emptyList());
        
        return allAreas.stream()
                .filter(area -> nearestGroupNames.contains(area.getAreaName()))
                .collect(Collectors.toList());
    }

    private Area findNearestAreaInGroup(List<String> groupNames, List<Area> allAreas, double midLat, double midLng) {
        return allAreas.stream()
                .filter(area -> groupNames.contains(area.getAreaName()))
                .min(Comparator.comparingDouble(area ->
                    DistanceCalculator.distance(midLat, midLng, area.getLatitude(), area.getLongitude())
                ))
                .orElse(null);
    }

    // 시간 문자열 파싱 메서드들
    private LocalTime parseMeetTime(String meetTime) {
        if (meetTime == null || meetTime.trim().isEmpty()) {
            return LocalTime.of(12, 0); // 기본값: 12:00
        }
        try {
            // "14:30" 형태나 "14:30:00" 형태 지원
            if (meetTime.length() == 5) {
                return LocalTime.parse(meetTime, DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                return LocalTime.parse(meetTime);
            }
        } catch (Exception e) {
            return LocalTime.of(12, 0); // 파싱 실패시 기본값
        }
    }

    private DayOfWeek parseMeetDay(String meetDays) {
        if (meetDays == null || meetDays.trim().isEmpty()) {
            return DayOfWeek.SATURDAY; // 기본값: 토요일
        }
        
        String day = meetDays.trim().toUpperCase();
        switch (day) {
            case "월요일", "월", "MONDAY": return DayOfWeek.MONDAY;
            case "화요일", "화", "TUESDAY": return DayOfWeek.TUESDAY; 
            case "수요일", "수", "WEDNESDAY": return DayOfWeek.WEDNESDAY;
            case "목요일", "목", "THURSDAY": return DayOfWeek.THURSDAY;
            case "금요일", "금", "FRIDAY": return DayOfWeek.FRIDAY;
            case "토요일", "토", "SATURDAY": return DayOfWeek.SATURDAY;
            case "일요일", "일", "SUNDAY": return DayOfWeek.SUNDAY;
            default: return DayOfWeek.SATURDAY; // 기본값
        }
    }

    private boolean isOpenDuringMeetTime(Place place, LocalTime meetTime, DayOfWeek meetDay) {
        if (place.getBusinessHours() == null) {
            return true; // 영업시간 정보가 없으면 영업중으로 가정
        }
        
        // 모임 시간에 2시간 정도 여유를 두고 체크 (모임이 길어질 수 있으니)
        LocalTime endTime = meetTime.plusHours(2);
        
        return place.isOpenDuring(meetTime, endTime, meetDay);
    }
}
