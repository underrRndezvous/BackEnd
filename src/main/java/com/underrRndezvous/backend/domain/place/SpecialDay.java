package com.underrRndezvous.backend.domain.place;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialDay {
    
    private LocalDate date;
    private boolean closed;
    private String reason;
    private LocalTime open;
    private LocalTime close;
    private String note;
    
    public boolean isTimeInRange(LocalTime time) {
        if (closed || open == null || close == null) {
            return false;
        }
        
        // 자정을 넘어가는 경우 처리
        if (close.isBefore(open)) {
            return time.isAfter(open) || time.isBefore(close);
        }
        
        return !time.isBefore(open) && !time.isAfter(close);
    }
}