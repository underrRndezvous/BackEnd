package com.underrRndezvous.backend.domain.place;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessHours {
    
    private BusinessType type;
    private Map<String, DaySchedule> regular;
    private List<SpecialDay> special;
    private String rawText;
    
    public enum BusinessType {
        REGULAR, HOURS_24, CLOSED, IRREGULAR
    }
    
    public DaySchedule getScheduleForDay(DayOfWeek dayOfWeek) {
        String dayKey = dayOfWeek.name().toLowerCase();
        return regular != null ? regular.get(dayKey) : null;
    }
    
    public boolean isOpenAt(LocalDateTime dateTime) {
        // 특별 운영일 체크
        if (special != null) {
            for (SpecialDay specialDay : special) {
                if (specialDay.getDate().equals(dateTime.toLocalDate())) {
                    if (specialDay.isClosed()) {
                        return false;
                    }
                    return specialDay.isTimeInRange(dateTime.toLocalTime());
                }
            }
        }
        
        // 24시간 영업 체크
        if (type == BusinessType.HOURS_24) {
            return true;
        }
        
        // 정기 운영시간 체크
        DaySchedule schedule = getScheduleForDay(dateTime.getDayOfWeek());
        if (schedule == null || schedule.isClosed()) {
            return false;
        }
        
        return schedule.isTimeInRange(dateTime.toLocalTime());
    }
    
    public boolean isOpenDuring(LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek) {
        if (type == BusinessType.HOURS_24) {
            return true;
        }
        
        DaySchedule schedule = getScheduleForDay(dayOfWeek);
        if (schedule == null || schedule.isClosed()) {
            return false;
        }
        
        return schedule.isOpenDuring(startTime, endTime);
    }
}