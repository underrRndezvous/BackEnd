package com.underrRndezvous.backend.repository;

import com.underrRndezvous.backend.domain.place.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findFirstBySiAndGuAndDong(String si, String gu, String dong);


}
