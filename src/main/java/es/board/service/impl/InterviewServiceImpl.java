package es.board.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.mapper.MainFunctionMapper;

import es.board.controller.model.req.InterviewQuestionDTO;

import es.board.controller.model.res.InterviewAnswerDTO;
import es.board.repository.entity.InterviewQuestion;
import es.board.repository.entity.PointHistory;
import es.board.repository.entity.entityrepository.InterviewQuestionAnswerRepository;
import es.board.repository.entity.entityrepository.InterviewQuestionRepository;
import es.board.repository.entity.entityrepository.PointHistoryRepository;
import es.board.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewServiceImpl implements InterviewService {

    private final InterviewQuestionRepository questionRepository;

    private final StringRedisTemplate redisTemplate;

    private  final ObjectMapper objectMapper;

    private  final PointHistoryRepository pointHistoryRepository;

    private  final InterviewQuestionAnswerRepository answerRepository;

    private  final MainFunctionMapper mapper;

    private static final String INTERVIEW_CACHE_KEY = "random_interview_question";

    private  static   final String  ANSWER_CACHE_KEY =  "answer_interview_question";


    @Override
    public String getTodayQuestion() {
        String question = redisTemplate.opsForValue().get(INTERVIEW_CACHE_KEY);
        if (question == null) {
            question = fetchRandomQuestion();
            redisTemplate.opsForValue().set(INTERVIEW_CACHE_KEY, question, Duration.ofDays(1));
        }

        return question;
    }
    @Override
    public List<InterviewQuestionDTO> getRandomQuestions() {
        String cachedData = redisTemplate.opsForValue().get(INTERVIEW_CACHE_KEY);

        if (cachedData != null) {
            return deserializeJson(cachedData);
        }
        List<InterviewQuestionDTO> questions = getCollect();
        cacheData(questions);

        return questions;
    }

    private List<InterviewQuestionDTO> getCollect() {
        return questionRepository.findRandomITAndGeneralQuestions().stream()
                .map(question -> InterviewQuestionDTO.builder()
                        .id(question.getId())
                        .question(question.getQuestion())
                        .category(question.getCategory())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void saveQuestion(InterviewAnswerDTO dto,String  userId) {
        String cacheKey = (dto.getCategory().equals("IT") ? "IT_" : "GENERAL_") + ANSWER_CACHE_KEY + "_" + userId;
        redisTemplate.opsForValue().set(cacheKey, "true", Duration.ofDays(1));
        answerRepository.save(mapper.toInterviewEntity(dto,userId));
        grantInterviewPoint(userId);
    }

    private String fetchRandomQuestion() {
        int randomIndex = new Random().nextInt(322) + 1;
        return questionRepository.findById((long) randomIndex)
                .map(InterviewQuestion::getQuestion)
                .orElse("오늘의 질문을 찾을 수 없습니다.");
    }
    private List<InterviewQuestionDTO> deserializeJson(String jsonData) {
        try {
            return objectMapper.readValue(jsonData, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return getCollect();
        }
    }

    private void cacheData(List<InterviewQuestionDTO> questions) {
        try {
            String jsonData = objectMapper.writeValueAsString(questions);
            redisTemplate.opsForValue().set(INTERVIEW_CACHE_KEY, jsonData, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void grantInterviewPoint(String userId) {
        String today = LocalDate.now().toString();
        String key = "comment:point:" + userId + ":" + today;
        Long currentCount = redisTemplate.opsForValue().increment(key);
        if (currentCount == 1) {
         redisTemplate.expire(key, Duration.ofDays(1));
        }
        if (currentCount > 10) {
            log.info("{}님은 오늘 댓글 작성 포인트 한도(10개)를 초과했습니다.", userId);
            return;
        }
        createPointHistory(userId);
        log.info("댓글 작성 포인트 지급 완료! 현재 작성 횟수: {}", currentCount);
    }
    public void createPointHistory(String userId) {
        PointHistory history = PointHistory.builder()
                .userId(userId)
                .pointChange(15)
                .reason("면접대답")
                .createdAt(LocalDateTime.now())
                .build();
        pointHistoryRepository.save(history);
    }
}
