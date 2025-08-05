package com.underrRndezvous.backend.repository;

import com.underrRndezvous.backend.domain.user.Location;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LocationRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    LocationRepository locationRepository;

    private Location location;

    @BeforeEach
    void setUp() {
        location = Location.of(null, "서울특별시", "종로구", "혜화동", 37.587817, 127.001745);
        em.persist(location);
        em.flush();
    }

    @Test
    @DisplayName("저장된 Location을 ID로 조회할 수 있어야 한다")
    void findById_Success() {
        Optional<Location> found = locationRepository.findById(location.getLocationId());
        assertThat(found).isPresent();
        assertThat(found.get().getDong()).isEqualTo("혜화동");
    }

    @Test
    @DisplayName("findAll 호출 시 저장된 Location이 포함되어야 한다")
    void findAll_ContainsPersisted() {
        List<Location> all = locationRepository.findAll();
        assertThat(all)
                .extracting(Location::getDong)
                .contains("역삼동");
    }
}
