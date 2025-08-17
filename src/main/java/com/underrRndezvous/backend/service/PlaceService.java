package com.underrRndezvous.backend.service;

import com.underrRndezvous.backend.controller.dto.response.PlaceDetailResponse;
import com.underrRndezvous.backend.domain.place.Location;
import com.underrRndezvous.backend.domain.place.Place;
import com.underrRndezvous.backend.exception.base.NotExistBaseException;
import com.underrRndezvous.backend.repository.LocationRepository;
import com.underrRndezvous.backend.repository.PlaceRepository;
import com.underrRndezvous.backend.dto.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final LocationRepository locationRepo;
    private final PlaceRepository placeRepo;

    // 행정동 -> 위도, 경도
    public Position getPositionFromArea(String si, String gu, String dong) {
        Location location = locationRepo.findFirstBySiAndGuAndDong(si, gu, dong)
                .orElseThrow(() -> new IllegalArgumentException("Unknown area: " + si + ", " + gu + ", " + dong));
        return new Position(location.getLatitude(), location.getLongitude());
    }


    public PlaceDetailResponse getPlaceDetail(Long storeId) {
        Place place = placeRepo.findById(storeId)
                .orElseThrow(() -> new NotExistBaseException("가게를 찾을 수 없습니다. id=" + storeId));

        return new PlaceDetailResponse(
                place.getId(),
                place.getType().name().toLowerCase(),
                place.getSubCategoryName(),
                place.getName(),
                0.0, // 평점은 임의로 0점
                place.getReviewCount(),
                place.getAddress(),
                place.getBusinessHours() != null ? place.getBusinessHours().toString() : "",
                "", // 이미지
                place.getSubCategoryName()
        );
    }
}