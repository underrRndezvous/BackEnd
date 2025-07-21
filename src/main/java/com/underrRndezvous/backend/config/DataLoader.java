package com.underrRndezvous.backend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.underrRndezvous.backend.domain.place.Area;
import com.underrRndezvous.backend.domain.user.Location;
import com.underrRndezvous.backend.dto.AreaDto;
import com.underrRndezvous.backend.dto.LocationDto;
import com.underrRndezvous.backend.repository.AreaRepository;
import com.underrRndezvous.backend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {
    private final LocationRepository locationRepo;
    private final AreaRepository     areaRepo;
    private final ObjectMapper       mapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1) 행정동 정보 로드 (csvjson.json)
        try (InputStream in = new ClassPathResource("csvjson.json").getInputStream()) {
            List<LocationDto> locDtos = mapper.readValue(in,
                    new TypeReference<List<LocationDto>>() {});
            List<Location> locations = locDtos.stream()
                    .map(dto -> new Location(
                            null,
                            dto.getSido(),
                            dto.getGu(),
                            dto.getDong(),
                            dto.getLat(),
                            dto.getLng()
                    ))
                    .collect(Collectors.toList());
            locationRepo.saveAll(locations);
        }

        // 2) 핫플 정보 로드 (hotplace.json)
        try (InputStream in = new ClassPathResource("hotplace.json").getInputStream()) {
            List<AreaDto> areaDtos = mapper.readValue(in,
                    new TypeReference<List<AreaDto>>() {});
            List<Area> areas = areaDtos.stream()
                    .map(dto -> new Area(
                            null,
                            dto.getAreaName(),
                            dto.getLatitude(),
                            dto.getLongitude()
                    ))
                    .collect(Collectors.toList());
            areaRepo.saveAll(areas);
        }
    }
}

