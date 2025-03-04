package es.board.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.req.CertificateDTO;
import es.board.repository.CertificateDAO;
import es.board.repository.document.Certificate;
import es.board.repository.entity.CertificationSchedule;
import es.board.repository.entity.entityrepository.CertificationScheduleRepository;
import es.board.service.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.bouncycastle.cert.cmp.CertificateStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateServiceImpl implements CertificateService {


    private static final String REDIS_PREFIX = "certificate:";

    private  final CertificateDAO certificateDAO;

    private  final ObjectMapper objectMapper;

    private static final long SEARCH_REQUEST_DELAY = 300;
    private static final Duration DEFAULT_TTL = Duration.ofHours(24);
    private static final Duration SHORT_TTL = Duration.ofHours(6);

    private static final String SEARCH_RATE_LIMIT_PREFIX = "search_limit:";

    private final RedisTemplate<String, Object> redisTemplate;


    private  final CertificationScheduleRepository scheduleRepository;


    @Override
    public List<String> getTop5CertificateCount() {

        return certificateDAO.findTop5CertificateCount();
    }

    @Override
    public List<Certificate> getCertificate(String text, String userIp) {
        if (isInvalidInput(text)) {
            throw new IllegalArgumentException("올바르지 않은 검색어입니다.");
        }

        String redisKey = REDIS_PREFIX + text;

        String rateLimitKey = SEARCH_RATE_LIMIT_PREFIX + userIp;
        try {

            Long lastSearchTime = (Long) redisTemplate.opsForValue().get(rateLimitKey);
            long currentTime = System.currentTimeMillis();

            if (lastSearchTime != null && (currentTime - lastSearchTime) < SEARCH_REQUEST_DELAY) {
                log.info("⏳ 검색 제한 적용 ({}ms 이내 중복 요청 차단)", SEARCH_REQUEST_DELAY);
                return redisTemplate.opsForValue().get(redisKey) != null
                        ? objectMapper.readValue((String) redisTemplate.opsForValue().get(redisKey), new TypeReference<List<Certificate>>() {})
                        : new ArrayList<>();
            }


            redisTemplate.opsForValue().set(rateLimitKey, currentTime, Duration.ofMillis(SEARCH_REQUEST_DELAY));


            String cachedJson = (String) redisTemplate.opsForValue().get(redisKey);
            if (cachedJson != null) {
                List<Certificate> cachedResults = objectMapper.readValue(cachedJson, new TypeReference<List<Certificate>>() {});
                log.info("✅ 캐시 결과: {}", cachedResults);
                return cachedResults;
            }
        } catch (Exception e) {
            log.error("Redis 캐시 변환 실패: {}", e.getMessage());
        }


        log.info(" 캐시 실패={}", text);
        List<Certificate> result = certificateDAO.findSearchCertificate(text);

        if (!result.isEmpty()) {
            try {
                String jsonResult = objectMapper.writeValueAsString(result);
                redisTemplate.opsForValue().set(redisKey, jsonResult, DEFAULT_TTL);
            } catch (Exception e) {
                log.error(" Redis 저장 실패: {}", e.getMessage());
            }
        } else {
            redisTemplate.opsForValue().set(redisKey, "[]", SHORT_TTL);
        }

        return result;
    }

    @Override
    public List<String> getMainCategoryAndSubCategory(String mainCategory, String subCategory) {
        return scheduleRepository.findAllByMajorCategoryAndSubCategory(mainCategory,subCategory);
    }

    @Override
    public List<CertificationSchedule> getCertificationSchedule(String text){

        return  scheduleRepository.findAllByCertificationSchedule(text);
    }



    private static boolean isInvalidInput(String input) {
        return Stream.of(
                isTooShort(input),
                isMeaninglessInput(input),
                containsOnlyConsonantsOrVowels(input),
                isSequentialPattern(input)
        ).anyMatch(Boolean::booleanValue);
    }



    private static boolean isTooShort(String input) {
        return input.length() < 2;
    }

    private static boolean isMeaninglessInput(String input) {
        return input.matches("^(.)\\1{3,}$");
    }

    private static boolean containsOnlyConsonantsOrVowels(String input) {
        return input.matches("^[ㄱ-ㅎㅏ-ㅣa-zA-Z]+$");
    }


    private static boolean isSequentialPattern(String input) {
        return Stream.of("ㄱㄴㄷㄹ", "ㅁㅇㅁㅇ", "ㅋㅋㅋ", "ㅎㅎㅎ")
                .anyMatch(input::contains);
    }
}
