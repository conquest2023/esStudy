package es.board.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.mapper.InterviewLogFactory;
import es.board.controller.model.mapper.MainFunctionMapper;

import es.board.controller.model.dto.interview.InterviewQuestionDTO;

import es.board.controller.model.dto.interview.InterviewAnswerDTO;
import es.board.controller.model.dto.interview.InterviewLogDTO;
import es.board.repository.InterviewQuestionDAO;
import es.board.repository.LogDAO;
import es.board.repository.entity.InterviewQuestion;
import es.board.repository.entity.PointHistoryEntity;
import es.board.repository.entity.repository.InterviewQuestionAnswerRepository;
import es.board.repository.entity.repository.InterviewQuestionRepository;
import es.board.repository.entity.repository.PointHistoryRepository;
import es.board.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewServiceImpl implements InterviewService {

    private static final String INTERVIEW_CACHE_KEY = "random_interview_question_3";

    private  static   final String  ANSWER_CACHE_KEY =  "answer_interview_question";


    private  static   final String  INTERVIEW_AGGREGATION_KEY =  "interview_aggregation_questions";

    private  final MainFunctionMapper mapper;

    private  final InterviewLogFactory factory;

    private  final ObjectMapper objectMapper;

    private final StringRedisTemplate redisTemplate;

    private  final LogDAO logDAO;

    private  final InterviewQuestionDAO questionDAO;

    private final InterviewQuestionRepository questionRepository;

    private  final PointHistoryRepository pointHistoryRepository;

    private  final InterviewQuestionAnswerRepository answerRepository;


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
    public  List<InterviewQuestion> getAggregationQuestion(String subCategory){
        String key = INTERVIEW_AGGREGATION_KEY + subCategory;
        String cachedData = redisTemplate.opsForValue().get(key);
        if (cachedData != null) {
            log.info("캐시를 성공했습니다");
            return deserializeJson(cachedData);
        }
        List<String> list = logDAO.aggregationInterviewQuestionLog(subCategory);
        List<InterviewQuestion> questions = questionRepository.findByIds(list);
        cacheData(key,questions);

        return  questions;
    }

    @Override
    public void saveLog(String token, InterviewLogDTO dto) {

        logDAO.saveLog("interview_logs",factory.createFrom(dto,token));

    }


    @Override
    public List<es.board.repository.document.InterviewQuestion> getSearchInterviewQuestion(String text) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("검색어는 필수입니다.");
        }
        return   questionDAO.searchInterviewQuestion(text);
    }

    @Override
    public Page<InterviewQuestion> getCategoryQuestion(String category, String subCategory, int page , int size) {

        Pageable pageable = PageRequest.of(page, size);
        return questionRepository.findByCategoryAndSubCategory(category,subCategory,pageable);
    }

    @Override
    public List<InterviewQuestionDTO> getRandomQuestions() {
        String cachedData = redisTemplate.opsForValue().get(INTERVIEW_CACHE_KEY);

        if (cachedData != null) {
            return deserializeJson(cachedData);
        }
        List<InterviewQuestionDTO> questions = getCollect();
        cacheData(INTERVIEW_CACHE_KEY,questions);

        return questions;
    }

    @Override
    public List<InterviewAnswerDTO> getBestAnswer(List<String> ids) {
        return mapper.fromAnswerList(answerRepository.findInterviewAnswerByQuestion(ids));
    }

    @Override
    public void saveQuestion(InterviewAnswerDTO dto,String  userId, String username) {
        checkQuestion(dto.getAnswer());
//        String categoryPrefix = dto.getCategory().equals("IT") ? "IT_" : "GENERAL_";
//        String cacheKey = categoryPrefix + ANSWER_CACHE_KEY + "_" + userId;
//
//        Integer count = redisTemplate.opsForValue().get(cacheKey) == null ? 0 :
//                Integer.parseInt(redisTemplate.opsForValue().get(cacheKey));
//
//        if (count >= 5) {
//            throw new IllegalStateException("오늘은 더 이상 답변을 작성할 수 없습니다.");
//        }
//        redisTemplate.opsForValue().set(cacheKey, String.valueOf(count + 1), Duration.ofDays(1));
        answerRepository.save(mapper.toInterviewEntity(dto, userId,username));
        grantInterviewPoint(userId,username);
    }

    private String fetchRandomQuestion() {
        int randomIndex = new Random().nextInt(322) + 1;
        return questionRepository.findById((long) randomIndex)
                .map(InterviewQuestion::getQuestion)
                .orElse("오늘의 질문을 찾을 수 없습니다.");
    }
    private <T> List<T> deserializeJson(String jsonData) {
        try {
            return objectMapper.readValue(jsonData, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private  List<InterviewQuestionDTO> getCollect() {
        return questionRepository.findRandomITAndGeneralQuestions().stream()
                .map(question -> InterviewQuestionDTO.builder()
                        .id(question.getId())
                        .question(question.getQuestion())
                        .category(question.getCategory())
                        .build())
                .collect(Collectors.toList());
    }

    private <T> void cacheData(String key, List<T> data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void grantInterviewPoint(String userId,String username) {
        String today = LocalDate.now().toString();
        String key = "interview:point:" + userId + ":" + today;
        Long currentCount = redisTemplate.opsForValue().increment(key);
        if (currentCount == 1) {
         redisTemplate.expire(key, Duration.ofDays(1));
        }
        if (currentCount > 3) {
            log.info("{}님은 오늘 인터뷰 작성 포인트 한도를 초과했습니다.", userId);
            return;
        }
        createPointHistory(userId,username);
        log.info("인터뷰 작성 포인트 지급 완료! 현재 작성 횟수: {}", currentCount);
    }
    public void createPointHistory(String userId,String username) {
        PointHistoryEntity history = PointHistoryEntity.builder()
                .userId(userId)
                .username(username)
                .pointChange(15)
                .reason("면접대답")
                .createdAt(LocalDateTime.now())
                .build();
        pointHistoryRepository.save(history);
    }

    private boolean checkQuestion(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("답변을 입력해 주세요");
        }
        if (content.length() < 35) {
            throw new RuntimeException("35자 이상 써주세요");
        }
        long totalLength = content.length();
        long chosungCount = content.chars()
                .filter(ch -> ch >= 0x3131 && ch <= 0x314E)
                .count();

        if ((double) chosungCount / totalLength > 0.5) {
            throw new RuntimeException("초성은 제외시켜주세요");
        }
        return true;
    }

}
