package com.underrRndezvous.backend.service;

import com.underrRndezvous.backend.controller.dto.response.AreaResponse;
import com.underrRndezvous.backend.domain.place.Area;

import com.underrRndezvous.backend.domain.user.MeetingUser;
import com.underrRndezvous.backend.exception.base.NotExistBaseException;
import com.underrRndezvous.backend.repository.AreaRepository;
import com.underrRndezvous.backend.repository.MeetingUserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AreaRecommendationService {

    private final MeetingUserRepository meetingUserRepo;
    private final AreaRepository         areaRepo;

    public AreaRecommendationService(MeetingUserRepository meetingUserRepo,
                                     AreaRepository areaRepo) {
        this.meetingUserRepo = meetingUserRepo;
        this.areaRepo        = areaRepo;
    }


    // 모임(meetingId) 참가자들의 중간 위치에서 가장 가까운 핫플 3곳을 반환
    public List<AreaResponse> recommendByMeeting(Long meetingId, int limit) {
        List<MeetingUser> participants = meetingUserRepo.findByMeetingMeetingId(meetingId);
        if (participants.isEmpty()) {
            throw new NotExistBaseException("Meeting or participants not found: " + meetingId);
        }

        double[] midpoint = calculateGeographicMidpoint(participants);
        double midLat = midpoint[0];
        double midLng = midpoint[1];

        return areaRepo.findAll().stream()
                .map(area -> new AreaResponse(
                        area.getAreaId(),
                        area.getAreaName(),
                        area.getLatitude(),
                        area.getLongitude(),
                        haversine(midLat, midLng,
                                area.getLatitude(), area.getLongitude())
                ))
                .sorted(Comparator.comparingDouble(AreaResponse::getDistance))
                .limit(limit)
                .collect(Collectors.toList());
    }


    // 지구 곡면 위 참가자들의 중간 지점을 계산
    private double[] calculateGeographicMidpoint(List<MeetingUser> participants) {
        double x = 0, y = 0, z = 0;

        for (MeetingUser mu : participants) {
            double lat = Math.toRadians(mu.getLocation().getLatitude());
            double lon = Math.toRadians(mu.getLocation().getLongitude());

            x += Math.cos(lat) * Math.cos(lon);
            y += Math.cos(lat) * Math.sin(lon);
            z += Math.sin(lat);
        }

        int total = participants.size();
        x /= total;
        y /= total;
        z /= total;

        double hyp = Math.sqrt(x * x + y * y);
        double midLat = Math.toDegrees(Math.atan2(z, hyp));
        double midLng = Math.toDegrees(Math.atan2(y, x));

        return new double[]{midLat, midLng};
    }


    private static final double R = 6371;
    private double haversine(double lat1, double lon1,
                             double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}