package com.underrRndezvous.backend.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum AreaGroup {
    SEONGSU_GROUP("성수권", List.of("성수", "왕십리", "건대입구")),
    HONGDAE_GROUP("홍대권", List.of("홍대입구", "합정", "신촌")),
    JONGNO_GROUP("종로권", List.of("종로3가", "안국", "을지로3가")),
    HYEHWA_GROUP("혜화권", List.of("혜화", "성신여대", "동대문역사문화공원")),
    JAMSIL_GROUP("잠실권", List.of("잠실", "석촌", "종합운동장")),
    GANGNAM_GROUP("강남권", List.of("강남", "신논현", "선릉")),
    SINSA_GROUP("신사권", List.of("신사", "압구정로데오", "압구정")),
    YEOUIDO_GROUP("여의도권", List.of("여의도", "신도림", "노량진")),
    YONGSAN_GROUP("용산권", List.of("용산", "삼각지", "이태원")),
    SADANG_GROUP("사당권", List.of("사당", "이수", "서울대입구"));

    private final String groupName;
    private final List<String> areaNames;

    public static Optional<AreaGroup> findByAreaName(String areaName) {
        return Arrays.stream(values())
                .filter(group -> group.areaNames.contains(areaName))
                .findFirst();
    }

    public boolean containsArea(String areaName) {
        return areaNames.contains(areaName);
    }

    public static List<List<String>> getAllAreaGroups() {
        return Arrays.stream(values())
                .map(AreaGroup::getAreaNames)
                .collect(Collectors.toList());
    }
}