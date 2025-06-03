package es.board.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.req.InterviewQuestionRequest;
import es.board.repository.entity.DailyQuestion;
import es.board.repository.entity.repository.DailyQuestionRepository;
import es.board.service.DailyQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyQuestionServiceImpl implements DailyQuestionService {

    private static final String  TOEIC_CACHE_KEY = "random_toeic_question";

    private  static   final String  CIVIL_CACHE_KEY =  "random_civil_question";


    private  final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;


    private  final DailyQuestionRepository dailyQuestionRepository;


    @Override
    public List<DailyQuestion> findToeicDailyQuestion(String category) {
        String caches = redisTemplate.opsForValue().get(TOEIC_CACHE_KEY);
        if (caches != null) {
            return deserializeJson(category,caches);
            }
            List<DailyQuestion> questions = dailyQuestionRepository.findDailyQuestionByToeic(category);
            cacheData(questions);
            return  questions;
    }

    @Override
    public List<DailyQuestion> findCivilDailyQuestion(String category) {
        String caches = redisTemplate.opsForValue().get(CIVIL_CACHE_KEY);
        if (caches != null) {
            log.info("공무원 문제 캐시 성공");
            return deserializeCivilJson(category,caches);
        }
        List<DailyQuestion> questions = dailyQuestionRepository.findDailyQuestionByCivil(category);
        cacheCivilData(questions);
        return  questions;

    }


    private List<DailyQuestion> deserializeJson(String category, String jsonData) {
        try {
            return objectMapper.readValue(jsonData, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return dailyQuestionRepository.findDailyQuestionByToeic(category);

        }
    }

    private List<DailyQuestion> deserializeCivilJson(String category, String jsonData) {
        try {
            return objectMapper.readValue(jsonData, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return dailyQuestionRepository.findDailyQuestionByCivil(category);

        }
    }
    private void cacheCivilData(List<DailyQuestion> questions) {
        try {
            String jsonData = objectMapper.writeValueAsString(questions);
            redisTemplate.opsForValue().set(CIVIL_CACHE_KEY, jsonData, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void cacheData(List<DailyQuestion> questions) {
        try {
            String jsonData = objectMapper.writeValueAsString(questions);
            redisTemplate.opsForValue().set(TOEIC_CACHE_KEY, jsonData, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
