# BusinessHours 데이터베이스 적용 가이드

## 1. 데이터베이스 스키마 변경

### MySQL/MariaDB
```sql
-- places 테이블의 business_hours 컬럼을 JSON 타입으로 변경
ALTER TABLE places MODIFY COLUMN business_hours JSON;

-- 또는 새로 생성하는 경우
CREATE TABLE places (
    place_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_name VARCHAR(255) NOT NULL,
    business_hours JSON,
    -- 다른 컬럼들...
);
```

### PostgreSQL
```sql
-- places 테이블의 business_hours 컬럼을 JSONB 타입으로 변경
ALTER TABLE places ALTER COLUMN business_hours TYPE JSONB USING business_hours::JSONB;

-- 인덱스 생성 (성능 향상)
CREATE INDEX idx_places_business_hours ON places USING GIN (business_hours);
```

## 2. 기존 데이터 마이그레이션

### 2.1 CSV 데이터 파싱 및 변환 스크립트

```java
@Component
public class BusinessHoursMigration {
    
    @Autowired
    private PlaceRepository placeRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public void migrateFromCsv(String csvFilePath) throws IOException {
        List<String[]> csvData = readCsvFile(csvFilePath);
        
        for (String[] row : csvData) {
            Long placeId = Long.parseLong(row[0]); // place_id
            String rawBusinessHours = row[3]; // business_hours
            
            BusinessHours businessHours = parseBusinessHours(rawBusinessHours);
            
            // 데이터베이스 업데이트
            placeRepository.updateBusinessHours(placeId, 
                objectMapper.writeValueAsString(businessHours));
        }
    }
    
    private BusinessHours parseBusinessHours(String rawText) {
        BusinessHours businessHours = new BusinessHours();
        businessHours.setRawText(rawText);
        
        // 24시간 영업 체크
        if (rawText.contains("24시간 영업") || rawText.contains("연중무휴")) {
            businessHours.setType(BusinessHours.BusinessType.HOURS_24);
            businessHours.setRegular(create24HourSchedule());
            return businessHours;
        }
        
        // 정기 영업시간 파싱
        Map<String, DaySchedule> regular = parseRegularHours(rawText);
        businessHours.setRegular(regular);
        businessHours.setType(BusinessHours.BusinessType.REGULAR);
        
        return businessHours;
    }
    
    private Map<String, DaySchedule> parseRegularHours(String rawText) {
        Map<String, DaySchedule> schedule = new HashMap<>();
        
        // 요일별 파싱 로직 (복잡한 정규식 처리)
        String[] lines = rawText.split("\\n|\\|");
        
        for (String line : lines) {
            if (line.contains("월")) {
                schedule.put("monday", parseDaySchedule(line));
            } else if (line.contains("화")) {
                schedule.put("tuesday", parseDaySchedule(line));
            }
            // ... 나머지 요일들
        }
        
        return schedule;
    }
}
```

### 2.2 데이터 검증 스크립트

```sql
-- 마이그레이션 후 데이터 검증
SELECT 
    place_id,
    place_name,
    JSON_EXTRACT(business_hours, '$.type') as business_type,
    JSON_EXTRACT(business_hours, '$.regular.monday.open') as monday_open,
    JSON_EXTRACT(business_hours, '$.regular.monday.close') as monday_close
FROM places 
WHERE business_hours IS NOT NULL
LIMIT 10;
```

## 3. JSON 데이터 구조 예시

### 3.1 일반적인 영업시간
```json
{
  "type": "regular",
  "regular": {
    "monday": {
      "open": "11:00",
      "close": "22:00",
      "lastOrder": "21:30"
    },
    "tuesday": {
      "open": "11:00",
      "close": "22:00",
      "lastOrder": "21:30"
    },
    "wednesday": {
      "open": "11:00",
      "close": "22:00",
      "lastOrder": "21:30"
    },
    "thursday": {
      "open": "11:00",
      "close": "22:00",
      "lastOrder": "21:30"
    },
    "friday": {
      "open": "11:00",
      "close": "22:00",
      "lastOrder": "21:30"
    },
    "saturday": {
      "open": "11:00",
      "close": "22:00",
      "lastOrder": "21:30"
    },
    "sunday": {
      "closed": true,
      "reason": "정기휴무"
    }
  },
  "rawText": "월-토 11:00-22:00 (라스트오더 21:30) | 일요일 휴무"
}
```

### 3.2 브레이크타임이 있는 경우
```json
{
  "type": "regular",
  "regular": {
    "monday": {
      "open": "11:00",
      "close": "21:00",
      "breaks": [
        {
          "start": "15:00",
          "end": "17:00"
        }
      ],
      "lastOrder": "20:30"
    }
  }
}
```

### 3.3 24시간 영업
```json
{
  "type": "24hours",
  "regular": {
    "monday": {"open": "00:00", "close": "24:00"},
    "tuesday": {"open": "00:00", "close": "24:00"},
    "wednesday": {"open": "00:00", "close": "24:00"},
    "thursday": {"open": "00:00", "close": "24:00"},
    "friday": {"open": "00:00", "close": "24:00"},
    "saturday": {"open": "00:00", "close": "24:00"},
    "sunday": {"open": "00:00", "close": "24:00"}
  },
  "rawText": "24시간 영업 연중무휴"
}
```

## 4. 애플리케이션 설정

### 4.1 application.yml 설정
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
    show-sql: true
```

### 4.2 ObjectMapper 설정
```java
@Configuration
public class JacksonConfig {
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
```

## 5. 조회 쿼리 예시

### 5.1 현재 영업중인 음식점 조회
```java
@Repository
public class PlaceRepositoryImpl {
    
    @Query(value = """
        SELECT * FROM places p 
        WHERE JSON_EXTRACT(p.business_hours, '$.type') = 'regular'
        AND (
            JSON_EXTRACT(p.business_hours, CONCAT('$.regular.', LOWER(DAYNAME(NOW())), '.closed')) IS NULL
            OR JSON_EXTRACT(p.business_hours, CONCAT('$.regular.', LOWER(DAYNAME(NOW())), '.closed')) = false
        )
        AND TIME(NOW()) BETWEEN 
            TIME(JSON_UNQUOTE(JSON_EXTRACT(p.business_hours, CONCAT('$.regular.', LOWER(DAYNAME(NOW())), '.open'))))
            AND TIME(JSON_UNQUOTE(JSON_EXTRACT(p.business_hours, CONCAT('$.regular.', LOWER(DAYNAME(NOW())), '.close'))))
        """, nativeQuery = true)
    List<Place> findOpenPlaces();
}
```

### 5.2 Java에서 필터링 (권장)
```java
@Service
public class PlaceService {
    
    public List<Place> findOpenPlaces() {
        return placeRepository.findAll().stream()
            .filter(Place::isOpenNow)
            .collect(toList());
    }
    
    public List<Place> findPlacesOpenAt(LocalDateTime dateTime) {
        return placeRepository.findAll().stream()
            .filter(place -> place.isOpenAt(dateTime))
            .collect(toList());
    }
}
```

## 6. 테스트 코드 예시

```java
@Test
public void testBusinessHoursConversion() {
    // Given
    BusinessHours businessHours = new BusinessHours();
    businessHours.setType(BusinessHours.BusinessType.REGULAR);
    
    Map<String, DaySchedule> regular = new HashMap<>();
    DaySchedule mondaySchedule = new DaySchedule();
    mondaySchedule.setOpen(LocalTime.of(11, 0));
    mondaySchedule.setClose(LocalTime.of(22, 0));
    regular.put("monday", mondaySchedule);
    
    businessHours.setRegular(regular);
    
    // When
    Place place = new Place();
    place.setBusinessHours(businessHours);
    Place savedPlace = placeRepository.save(place);
    
    // Then
    assertThat(savedPlace.getBusinessHours().getRegular().get("monday").getOpen())
        .isEqualTo(LocalTime.of(11, 0));
}

@Test
public void testIsOpenAt() {
    // Given - 월요일 11:00-22:00 영업하는 음식점
    LocalDateTime mondayAfternoon = LocalDateTime.of(2024, 1, 15, 15, 30); // 월요일 15:30
    
    // When & Then
    assertTrue(place.isOpenAt(mondayAfternoon));
}
```

## 7. 운영 시 주의사항

### 7.1 성능 고려사항
- JSON 필드에 대한 복잡한 쿼리는 성능이 떨어질 수 있음
- 대용량 데이터의 경우 Java에서 필터링하는 것이 더 효율적
- 자주 조회하는 조건에 대해서는 별도 인덱스 컬럼 고려

### 7.2 데이터 백업
```sql
-- 마이그레이션 전 기존 데이터 백업
CREATE TABLE places_backup AS SELECT * FROM places;

-- 문제 발생 시 복구
INSERT INTO places SELECT * FROM places_backup WHERE place_id = ?;
```

### 7.3 점진적 마이그레이션
```java
// 기존 String 필드와 새 JSON 필드를 동시에 유지하며 점진적 이전
@Column(name = "business_hours_old")
private String businessHoursOld; // 기존 필드

@Convert(converter = BusinessHoursConverter.class)
@Column(name = "business_hours", columnDefinition = "JSON")
private BusinessHours businessHours; // 새 필드
```

이 가이드를 따라 단계별로 진행하면 안전하게 BusinessHours를 데이터베이스에 적용할 수 있습니다.