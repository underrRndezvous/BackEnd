package com.underrRndezvous.backend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.underrRndezvous.backend.domain.place.Area;
import com.underrRndezvous.backend.domain.place.Location;
import com.underrRndezvous.backend.dto.AreaDto;
import com.underrRndezvous.backend.dto.LocationDto;
import com.underrRndezvous.backend.repository.AreaRepository;
import com.underrRndezvous.backend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final ObjectMapper mapper;
    private final AreaRepository areaRepo;
    private final LocationRepository locRepo;

    @Value("classpath:csvjson.json")
    private Resource csvResource;

    @Value("classpath:hotplace.json")
    private Resource hotplaceResource;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // 1) Area 로딩 & 저장
        try (InputStream is = hotplaceResource.getInputStream()) {
            List<AreaDto> areas = mapper.readValue(is, new TypeReference<List<AreaDto>>() {});
            areas.stream()
                    .map(dto -> Area.builder()
                            .areaName(dto.getAreaName())
                            .areaImage(dto.getAreaImage())
                            .latitude(dto.getLatitude())
                            .longitude(dto.getLongitude())
                            .build()
                    )
                    .forEach(areaRepo::save);
        }

        // 2) Location 로딩 & 저장
        try (InputStream is = csvResource.getInputStream()) {
            List<LocationDto> locs = mapper.readValue(is, new TypeReference<List<LocationDto>>() {});
            locs.stream()
                    .map(dto -> Location.builder()
                            .si(dto.getSi())
                            .gu(dto.getGu())
                            .dong(dto.getDong())
                            .latitude(dto.getLat())
                            .longitude(dto.getLng())
                            .build()
                    )
                    .forEach(locRepo::save);
        }

    }
}