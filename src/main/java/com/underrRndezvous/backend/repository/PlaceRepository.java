package com.underrRndezvous.backend.repository;

import com.underrRndezvous.backend.domain.enums.PlaceType;
import com.underrRndezvous.backend.domain.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByAreaAreaIdAndTypeAndSubCategoryName(Long areaId, PlaceType type, String subCategoryName);
    List<Place> findByAreaAreaIdAndType(Long areaId, PlaceType type);
    List<Place> findByAreaAreaIdAndTypeAndAtmosphereContaining(Long areaId, PlaceType type, String atmosphere);
}