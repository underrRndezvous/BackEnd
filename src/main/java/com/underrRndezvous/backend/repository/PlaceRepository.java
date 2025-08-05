package com.underrRndezvous.backend.repository;

import com.underrRndezvous.backend.domain.enums.PlaceType;
import com.underrRndezvous.backend.domain.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByTypeAndSubCategory_Name(PlaceType type, String subCategoryName);
}