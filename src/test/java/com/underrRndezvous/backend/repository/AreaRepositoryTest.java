package com.underrRndezvous.backend.repository;

import com.underrRndezvous.backend.domain.place.Area;
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
public class AreaRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private AreaRepository areaRepository;

    private Area saved;

    @BeforeEach
    void setUp() {
        saved = new Area(null, "TestArea", 12.34, 56.78);
        em.persist(saved);
        em.flush();
    }

    @Test
    @DisplayName("저장된 Area를 ID로 조회할 수 있어야 한다")
    void findById_Success() {
        Optional<Area> opt = areaRepository.findById(saved.getAreaId());
        assertThat(opt).isPresent();
        assertThat(opt.get().getAreaName()).isEqualTo("TestArea");
        assertThat(opt.get().getLatitude()).isEqualTo(12.34);
        assertThat(opt.get().getLongitude()).isEqualTo(56.78);
    }

    @Test
    @DisplayName("findAll 호출 시 저장된 Area가 포함되어야 한다")
    void findAll_ContainsPersisted() {
        List<Area> all = areaRepository.findAll();
        assertThat(all)
                .extracting(Area::getAreaName)
                .contains("TestArea");
    }
}
