package com.underrRndezvous.backend.repository;

import com.underrRndezvous.backend.domain.user.Location;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LocationRepository extends JpaRepository<Location, Long> {

}
