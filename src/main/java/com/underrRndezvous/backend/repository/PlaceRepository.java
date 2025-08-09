package com.underrRndezvous.backend.repository;

import com.underrRndezvous.backend.domain.enums.PlaceType;
import com.underrRndezvous.backend.domain.enums.CafeAtmosphere;
import com.underrRndezvous.backend.domain.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByTypeAndSubCategoryName(PlaceType type, String subCategoryName);
    List<Place> findByTypeAndSubCategoryNameAndCafeAtmosphere(PlaceType type, String subCategoryName, CafeAtmosphere cafeAtmosphere);
    List<Place> findByAreaAreaIdAndTypeAndSubCategoryName(Long areaId, PlaceType type, String subCategoryName);
    List<Place> findByAreaAreaIdAndTypeAndSubCategoryNameAndCafeAtmosphere(Long areaId, PlaceType type, String subCategoryName, CafeAtmosphere cafeAtmosphere);
}