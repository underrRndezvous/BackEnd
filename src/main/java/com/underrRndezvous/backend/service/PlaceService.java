package com.underrRndezvous.backend.service;

import com.underrRndezvous.backend.domain.place.Location;
import com.underrRndezvous.backend.repository.LocationRepository;
import com.underrRndezvous.backend.util.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final LocationRepository locationRepo;

    // 행정동 -> 위도, 경도
    public Position getPositionFromArea(String si, String gu, String dong) {
        Location location = locationRepo.findBySiAndGuAndDong(si, gu, dong)
                .orElseThrow(() -> new IllegalArgumentException("Unknown area"));
        return new Position(location.getLatitude(), location.getLongitude());
    }

    // 지구 구면 중간 위치 계산
    public double[] calculateGeographicMidpoint(List<Position> positions) {
        double x=0, y=0, z=0;
        for (Position p : positions) {
            double lat = Math.toRadians(p.getLat());
            double lon = Math.toRadians(p.getLng());
            x += Math.cos(lat) * Math.cos(lon);
            y += Math.cos(lat) * Math.sin(lon);
            z += Math.sin(lat);
        }
        int n = positions.size();
        x/=n; y/=n; z/=n;
        double lon0 = Math.atan2(y, x);
        double hyp  = Math.sqrt(x*x + y*y);
        double lat0 = Math.atan2(z, hyp);
        return new double[]{ Math.toDegrees(lat0), Math.toDegrees(lon0) };
    }
}