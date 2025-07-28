package com.underrRndezvous.backend.repository;

import com.underrRndezvous.backend.domain.place.Area;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {

}
