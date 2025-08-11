package com.underrRndezvous.backend.domain.place;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;

@Slf4j
@Converter
public class BusinessHoursConverter implements AttributeConverter<BusinessHours, String> {
    
    private final ObjectMapper objectMapper;
    
    public BusinessHoursConverter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        
        // Custom module to handle 24:00 time format
        SimpleModule customTimeModule = new SimpleModule();
        customTimeModule.addDeserializer(LocalTime.class, new CustomLocalTimeDeserializer());
        this.objectMapper.registerModule(customTimeModule);
    }
    
    // Custom deserializer that handles "24:00" by converting it to "00:00"
    private static class CustomLocalTimeDeserializer extends LocalTimeDeserializer {
        @Override
        protected LocalTime _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
            String timeString = string0.trim();
            
            // Handle "24:00" as midnight (00:00)
            if ("24:00".equals(timeString)) {
                return LocalTime.MIDNIGHT;
            }
            
            // For other formats, use the parent implementation
            return super._fromString(p, ctxt, timeString);
        }
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