package com.underrRndezvous.backend.domain.place;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DaySchedule {
    
    private boolean closed;
    private LocalTime open;
    private LocalTime close;
    private LocalTime lastOrder;
    private LocalTime lunchLastOrder;
    private List<BreakTime> breaks;
    
    public boolean isTimeInRange(LocalTime time) {
        if (closed || open == null || close == null) {
            return false;
        }
        
        // 브레이크타임 체크
        if (breaks != null) {
            for (BreakTime breakTime : breaks) {
                if (breakTime.isTimeInBreak(time)) {
                    return false;
                }
            }
        }
        
        // 자정을 넘어가는 경우 처리 (예: 23:00 - 02:00)
        if (close.isBefore(open)) {
            return time.isAfter(open) || time.isBefore(close);
        }
        
        return !time.isBefore(open) && !time.isAfter(close);
    }
    
    public boolean isOpenDuring(LocalTime startTime, LocalTime endTime) {
        if (closed || open == null || close == null) {
            return false;
        }
        
        // 운영시간과 요청 시간대가 겹치는지 확인
        LocalTime requestStart = startTime;
        LocalTime requestEnd = endTime;
        
        // 자정을 넘어가는 경우 처리
        if (close.isBefore(open)) {
            // 24시간을 넘어가는 운영시간
            return !(requestEnd.isBefore(open) && requestStart.isAfter(close));
        }
        
        return !(requestEnd.isBefore(open) || requestStart.isAfter(close));
    }
    
    public boolean canOrder(LocalTime time) {
        if (!isTimeInRange(time)) {
            return false;
        }
        
        if (lastOrder != null && time.isAfter(lastOrder)) {
            return false;
        }
        
        return true;
    }
}