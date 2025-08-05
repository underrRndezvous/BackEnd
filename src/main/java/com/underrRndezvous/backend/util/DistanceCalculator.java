package com.underrRndezvous.backend.util;

import com.underrRndezvous.backend.dto.Position;

import java.util.List;

public class DistanceCalculator {
    private static final double R = 6371; // 지구 반지름

    // 지구 구면 중간 위치 계산
    public static double[] calculateGeographicMidpoint(List<Position> positions) {
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

    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        // 위도, 경도를 라디안으로 변환
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        // 위도 자체도 라디안으로 변환
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        // haversine 공식
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000;  // 거리(m) 반환
    }
}