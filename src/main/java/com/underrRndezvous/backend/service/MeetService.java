package com.underrRndezvous.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.underrRndezvous.backend.controller.dto.MeetRequest;
import com.underrRndezvous.backend.controller.dto.PlaceRequest;
import com.underrRndezvous.backend.controller.dto.response.MeetResponse;
import com.underrRndezvous.backend.controller.dto.response.PlaceRecommendation;
import com.underrRndezvous.backend.controller.dto.response.RegionRecommendation;
import com.underrRndezvous.backend.domain.enums.AreaGroup;
import com.underrRndezvous.backend.domain.enums.MeetingType;
import com.underrRndezvous.backend.domain.enums.PlaceType;
import com.underrRndezvous.backend.domain.enums.TimeType;
import com.underrRndezvous.backend.domain.enums.DayType;
import com.underrRndezvous.backend.domain.meeting.Meeting;
import com.underrRndezvous.backend.domain.meeting.User;
import com.underrRndezvous.backend.domain.place.Area;
import com.underrRndezvous.backend.domain.place.Place;
import com.underrRndezvous.backend.repository.AreaRepository;
import com.underrRndezvous.backend.repository.MeetingRepository;
import com.underrRndezvous.backend.repository.PlaceRepository;
import com.underrRndezvous.backend.repository.UserRepository;
import com.underrRndezvous.backend.util.DistanceCalculator;
import com.underrRndezvous.backend.dto.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
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
    private final MeetingRepository meetingRepo;
    private final UserRepository userRepo;
    private final ObjectMapper objectMapper;

    @Transactional
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

        // 4) 각 지역별로 요청된 서브카테고리별 가게 추천 (지역명 기준 중복 제거)
        Map<String, RegionRecommendation> regionMap = nearestAreas.stream()
                .map(area -> createRegionRecommendation(area, req))
                .collect(Collectors.toMap(
                    RegionRecommendation::getHotPlace, // 지역명을 키로 사용
                    Function.identity(), // 값은 RegionRecommendation 자체
                    (existing, replacement) -> { // 중복시 기존 것에 추가
                        List<PlaceRecommendation> combined = new ArrayList<>(existing.getRecommendPlace());
                        combined.addAll(replacement.getRecommendPlace());
                        return new RegionRecommendation(existing.getHotPlace(), existing.getHotPlaceImage(), combined);
                    }
                ));
        
        List<RegionRecommendation> regions = regionMap.values().stream()
                .map(this::removeDuplicateStores) // 각 지역별로 중복 상점 제거
                .filter(region -> !region.getRecommendPlace().isEmpty()) // 빈 추천 제거
                .limit(3) // 최대 3개 지역
                .collect(Collectors.toList());

        // 5) 추천 결과 자동 저장 (사용자 없이)
        TimeType timeType = req.getMeetTime() != null && !req.getMeetTime().isEmpty() 
                ? req.getMeetTime().get(0) 
                : TimeType.LUNCH;
        
        Meeting meeting = new Meeting(
                req.getGroupName() != null ? req.getGroupName() : "추천 모임",
                MeetingType.FRIENDS,
                timeType
        );
        
        Meeting savedMeeting = meetingRepo.save(meeting);
        
        // regions만 포함된 임시 response로 JSON 저장 (meetingId 없이)
        MeetResponse tempResponse = new MeetResponse(regions);
        String responseJson;
        try {
            responseJson = objectMapper.writeValueAsString(tempResponse);
        } catch (Exception e) {
            responseJson = "{}";
        }
        savedMeeting.setRecommendationResult(responseJson);
        meetingRepo.save(savedMeeting);
        
        // meeting ID가 포함된 최종 response 반환
        return new MeetResponse(savedMeeting.getMeetingId(), regions);
    }

    public Long saveMeetingWithRecommendation(MeetRequest req, Long userId) {
        try {
            MeetResponse response = recommend(req);
            
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            
            TimeType timeType = req.getMeetTime() != null && !req.getMeetTime().isEmpty() 
                    ? req.getMeetTime().get(0) 
                    : TimeType.LUNCH;
            
            Meeting meeting = Meeting.builder()
                    .name(req.getGroupName())
                    .category(MeetingType.FRIENDS)
                    .meetingTime(timeType)
                    .user(user)
                    .build();
            
            String responseJson = objectMapper.writeValueAsString(response);
            meeting.setRecommendationResult(responseJson);
            
            Meeting savedMeeting = meetingRepo.save(meeting);
            return savedMeeting.getMeetingId();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to save meeting recommendation", e);
        }
    }

    public MeetResponse getMeetingRecommendation(Long meetingId) {
        Meeting meeting = meetingRepo.findByMeetingId(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found with ID: " + meetingId));
        
        if (meeting.getRecommendationResult() == null) {
            throw new RuntimeException("No recommendation data found for meeting ID: " + meetingId);
        }

        System.out.println("meeting recommendation result: " + meeting.getRecommendationResult());

        try {
            // JSON을 Map으로 파싱한 후 regions 추출
            Map<String, Object> jsonMap = objectMapper.readValue(meeting.getRecommendationResult(), Map.class);
            List<RegionRecommendation> regions = objectMapper.convertValue(
                jsonMap.get("regions"), 
                new com.fasterxml.jackson.core.type.TypeReference<List<RegionRecommendation>>() {}
            );
            return new MeetResponse(meetingId, regions);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse recommendation data for meeting ID: " + meetingId, e);
        }
    }

    private RegionRecommendation removeDuplicateStores(RegionRecommendation region) {
        List<PlaceRecommendation> uniquePlaces = region.getRecommendPlace().stream()
                .collect(Collectors.toMap(
                        PlaceRecommendation::getStoreId, // storeId를 키로 사용
                        Function.identity(), // 값은 PlaceRecommendation 자체
                        (existing, replacement) -> existing // 중복시 기존 것 유지
                ))
                .values()
                .stream()
                .sorted(Comparator.comparingInt(PlaceRecommendation::getOrder))
                .collect(Collectors.toList());
                
        return new RegionRecommendation(region.getHotPlace(), region.getHotPlaceImage(), uniquePlaces);
    }

    private RegionRecommendation createRegionRecommendation(Area area, MeetRequest req) {
        String hotPlace = area.getAreaName();
        String imageUrl = area.getAreaImage();
        
        // 모임 시간 파싱 - 시간대별 구분
        // 오전: 04:00-11:00, 점심: 11:00-13:59, 오후: 14:00-17:59, 저녁: 18:00-04:00
        List<LocalTime> meetTimes = parseMeetTimesWithTimeZone(req.getMeetTime());
        DayOfWeek meetDay = parseMeetDay(req.getMeetDays());
        
        List<PlaceRecommendation> placeRecommendations = req.getPlace().stream()
                .flatMap(placeRequest -> findRandomPlaces(placeRequest, area, meetTimes, meetDay).stream())
                .sorted(Comparator.comparingInt(PlaceRecommendation::getOrder))
                .collect(Collectors.toList());
        
        return new RegionRecommendation(hotPlace, imageUrl, placeRecommendations);
    }

    private List<PlaceRecommendation> findRandomPlaces(PlaceRequest placeRequest, Area area, List<LocalTime> meetTimes, DayOfWeek meetDay) {
        PlaceType type = PlaceType.valueOf(placeRequest.getPlaceType().toUpperCase());
        String detail = placeRequest.getTypeDetail();
        
        List<Place> candidates;
        
        // 카페인 경우 분위기 필터링 적용 (부분 일치 검색)
        if (type == PlaceType.CAFE && placeRequest.getAtmosphere() != null) {
            String atmosphere = placeRequest.getAtmosphere();
            candidates = placeRepo.findByAreaAreaIdAndTypeAndAtmosphereContaining(area.getAreaId(), type, atmosphere);
        }
        // 레스토랑과 바인 경우 서브카테고리 필터링 적용
        else if ((type == PlaceType.RESTAURANT || type == PlaceType.BAR) && detail != null) {
            candidates = placeRepo.findByAreaAreaIdAndTypeAndSubCategoryName(area.getAreaId(), type, detail);
        }
        else {
            candidates = placeRepo.findByAreaAreaIdAndType(area.getAreaId(), type);
        }
        
        // 영업시간 필터링 적용 (모든 모임 시간에 영업중인 가게만)
        List<Place> filteredCandidates = candidates.stream()
                .filter(place -> meetTimes.stream().allMatch(meetTime -> isOpenDuringMeetTime(place, meetTime, meetDay)))
                .collect(Collectors.toList());
        
        // 랜덤 순서로 섞기
        List<Place> shuffledCandidates = new ArrayList<>(filteredCandidates);
        Collections.shuffle(shuffledCandidates);
        
        // 첫 번째 장소 선택
        List<PlaceRecommendation> recommendations = new ArrayList<>();

        if (!shuffledCandidates.isEmpty()) {
            Place firstPlace = shuffledCandidates.get(0);
            boolean firstPlaceIsOpen = meetTimes.stream().allMatch(meetTime -> isOpenDuringMeetTime(firstPlace, meetTime, meetDay));

            // 첫 번째 장소 추가
            recommendations.add(createPlaceRecommendation(placeRequest, firstPlace, firstPlaceIsOpen));

            // 첫 번째 장소가 닫혀있으면 열린 대안 찾기
            if (!firstPlaceIsOpen) {
                shuffledCandidates.stream()
                    .skip(1)
                    .filter(place -> meetTimes.stream().allMatch(meetTime -> isOpenDuringMeetTime(place, meetTime, meetDay)))
                    .findFirst()
                    .ifPresent(place -> recommendations.add(createPlaceRecommendation(placeRequest, place, true)));
            }
        }
        
        return recommendations;
    }

    private PlaceRecommendation createPlaceRecommendation(PlaceRequest placeRequest, Place place, boolean isOpen) {
        return new PlaceRecommendation(
                placeRequest.getId(),
                place.getId(),
                place.getType().toString(),
                place.getName(),
                place.getLatitude(),
                place.getLongitude(),
                isOpen,
                place.getAddress(),
                place.getSubCategoryName()
        );
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
                .limit(3) // 최대 3개 지역만 선택
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

    // 시간대별 시간 객체 생성 메서드 (여러 시간 지원)
    private List<LocalTime> parseMeetTimesWithTimeZone(List<TimeType> timeTypes) {
        if (timeTypes == null || timeTypes.isEmpty()) {
            return Arrays.asList(LocalTime.of(12, 0)); // 기본값: 12:00 (점심)
        }
        
        return timeTypes.stream()
                .map(this::parseMeetTimeWithTimeZone)
                .collect(Collectors.toList());
    }
    
    // 시간대별 시간 객체 생성 메서드
    private LocalTime parseMeetTimeWithTimeZone(TimeType timeType) {
        if (timeType == null) {
            return LocalTime.of(12, 0); // 기본값: 12:00 (점심)
        }
        
        // TimeType enum에 따른 기본 시간 설정
        switch (timeType) {
            case MORNING -> {
                return LocalTime.of(9, 0); // 오전 9:00
            }
            case LUNCH -> {
                return LocalTime.of(12, 0); // 점심 12:00
            }
            case AFTERNOON -> {
                return LocalTime.of(15, 0); // 오후 3:00
            }
            case EVENING -> {
                return LocalTime.of(19, 0); // 저녁 7:00
            }
            default -> {
                return LocalTime.of(12, 0); // 기본값
            }
        }
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

    private DayOfWeek parseMeetDay(DayType dayType) {
        if (dayType == null) {
            return DayOfWeek.SATURDAY; // 기본값: 토요일
        }
        
        // DayType enum에 따른 DayOfWeek 매핑
        switch (dayType) {
            case MONDAY -> {
                return DayOfWeek.MONDAY;
            }
            case TUESDAY -> {
                return DayOfWeek.TUESDAY;
            }
            case WEDNESDAY -> {
                return DayOfWeek.WEDNESDAY;
            }
            case THURSDAY -> {
                return DayOfWeek.THURSDAY;
            }
            case FRIDAY -> {
                return DayOfWeek.FRIDAY;
            }
            case SATURDAY -> {
                return DayOfWeek.SATURDAY;
            }
            case SUNDAY -> {
                return DayOfWeek.SUNDAY;
            }
            case WEEKDAY -> {
                return DayOfWeek.FRIDAY; // 평일 대표로 금요일
            }
            case WEEKEND -> {
                return DayOfWeek.SATURDAY; // 주말 대표로 토요일
            }
            default -> {
                return DayOfWeek.SATURDAY; // 기본값
            }
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
