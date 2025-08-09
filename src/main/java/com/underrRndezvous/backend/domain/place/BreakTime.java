package com.underrRndezvous.backend.domain.place;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BreakTime {
    
    private LocalTime start;
    private LocalTime end;
    
    public boolean isTimeInBreak(LocalTime time) {
        if (start == null || end == null) {
            return false;
        }
        
        return !time.isBefore(start) && !time.isAfter(end);
    }
}