package com.underrRndezvous.backend.domain.place;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class BusinessHoursConverter implements AttributeConverter<BusinessHours, String> {
    
    private final ObjectMapper objectMapper;
    
    public BusinessHoursConverter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    public String convertToDatabaseColumn(BusinessHours businessHours) {
        if (businessHours == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(businessHours);
        } catch (JsonProcessingException e) {
            log.error("Error converting BusinessHours to JSON", e);
            return null;
        }
    }
    
    @Override
    public BusinessHours convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.readValue(dbData, BusinessHours.class);
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to BusinessHours: {}", dbData, e);
            return null;
        }
    }
}