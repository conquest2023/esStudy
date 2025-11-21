package es.board.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.dto.feed.DailyCheckDTO;
import es.board.controller.model.dto.interview.DailyBookMark;
import es.board.repository.entity.Bookmark;
import es.board.repository.entity.DailyQuestion;
import es.board.repository.entity.user.User;
import es.board.repository.entity.repository.BookMarkRepository;
import es.board.repository.entity.repository.DailyQuestionRepository;
import es.board.repository.entity.repository.UserRepository;
import es.board.service.DailyQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyQuestionServiceImpl implements DailyQuestionService {

    private static final String TOEIC_CACHE_KEY = "random_toeic_questions";

    private static final String CIVIL_CACHE_KEY = "random_civil_questions";

    private static final String POLICE_CACHE_KEY = "random_police_office_questions";

    private static final Map<String, String> CACHE_KEYS = Map.of(
            "일반행정", CIVIL_CACHE_KEY,
            "토익", TOEIC_CACHE_KEY,
            "경찰", POLICE_CACHE_KEY
    );



    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private  final UserRepository userRepository;

    private  final BookMarkRepository  bookMarkRepository;

    private final DailyQuestionRepository dailyQuestionRepository;

    @Override
    public boolean checkDailyAnswer(String userId, DailyCheckDTO req) {
        String problemCacheKey = CACHE_KEYS.getOrDefault(req.getCategory(), POLICE_CACHE_KEY);
        String userCheckKey = problemCacheKey+req.getMatter() + ":" + userId;
        String submitted = redisTemplate.opsForValue().get(userCheckKey);
//        String caches= redisTemplate.opsForValue()
//                .get(CACHE_KEYS.getOrDefault(req.getCategory(), POLICE_CACHE_KEY));
//        String submitted = redisTemplate.opsForValue().get(CACHE_KEYS.getOrDefault(req.getCategory(), POLICE_CACHE_KEY)+":" + userId);
        if (submitted != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 문제를 제출하셨습니다");
        }
        try {
            String caches = redisTemplate.opsForValue().get(problemCacheKey);
            List<DailyQuestion> questions = objectMapper.readValue(caches, new TypeReference<List<DailyQuestion>>() {});
            boolean isCorrect = checkAnswer(problemCacheKey, questions, req);
            redisTemplate.opsForValue().set(userCheckKey, isCorrect ? "1" : "0", 1, TimeUnit.DAYS);
            return isCorrect;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DailyQuestion> findToeicDailyQuestion(String category) {
        String caches = redisTemplate.opsForValue().get(TOEIC_CACHE_KEY);
        log.info(caches);
        if (caches != null) {
            return deserializeJson(category, caches);
        }
        List<DailyQuestion> questions = dailyQuestionRepository.findDailyQuestionByToeic(category);
        cacheData(questions, category);
        return questions;
    }

    @Override
    public List<DailyQuestion> findCivilDailyQuestion(String category) {
        String caches = redisTemplate.opsForValue().get(CIVIL_CACHE_KEY);
        if (caches != null) {
            return deserializeJson(category, caches);
        }
        List<DailyQuestion> questions = dailyQuestionRepository.findDailyQuestionByCivil(category);
        cacheData(questions, category);
        return questions;

    }

    @Override
    public List<DailyQuestion> findPoliceDailyQuestion(String category) {
        String caches = redisTemplate.opsForValue().get(POLICE_CACHE_KEY);
        if (caches != null) {
            return deserializeJson(category, caches);
        }
        List<DailyQuestion> questions = dailyQuestionRepository.findDailyQuestionByPolice(category);
        cacheData(questions, category);
        return questions;
    }

    @Override
    public List<DailyQuestion> getDailyQuestion(String userId) {
//        bookMarkRepository.
        return  bookMarkRepository.findBookmarkedQuestionsByUserId(userId);
    }

    @Override
    public void saveDailyBookMark(String userId, DailyBookMark daily) {
        User user= userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        DailyQuestion question = dailyQuestionRepository.findById(daily.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("문제가 존재하지 않음"));

         Bookmark bookmark= Bookmark.builder()
                .user(user)
                .dailyQuestion(question)
                .category(daily.getCategory())
                .createdAt(LocalDateTime.now())
                .build();

         bookMarkRepository.save(bookmark);
    }


    private List<DailyQuestion> deserializeJson(String category, String jsonData) {
            try {
                return objectMapper.readValue(jsonData, new TypeReference<>() {
                });
            } catch (Exception e) {
                e.printStackTrace();
                return dailyQuestionRepository.findDailyQuestionByCivil(category);
            }
    }
    private void cacheData(List<DailyQuestion> questions, String category) {
        String cacheKey = CACHE_KEYS.getOrDefault(category, POLICE_CACHE_KEY);
        try {
            String jsonData = objectMapper.writeValueAsString(questions);
            redisTemplate.opsForValue().set(cacheKey, jsonData, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkAnswer(String cacheKey, List<DailyQuestion> questions, DailyCheckDTO req) {
       if (cacheKey.equals("random_toeic_questions")){
           return  toeicCheckAnswer(questions,req);
       }
        for (DailyQuestion q : questions) {
            if (q.getQuestion().equals(req.getMatter())) {
                String userAnswer = convertSymbolToNumber(req.getAnswer());
                log.info(userAnswer);
                String correctAnswer = convertSymbolToNumber(q.getAnswer().trim());
                log.info(correctAnswer);
                return userAnswer.equals(correctAnswer);
            }
        }
        return false;
    }

    private boolean toeicCheckAnswer(List<DailyQuestion> questions, DailyCheckDTO req) {
        log.info(req.toString());
        for (DailyQuestion q : questions) {
            if (q.getQuestion().equals(req.getMatter())) {
                String userAnswer = convertSymbolToNumber(req.getAnswer().trim());
                log.info(userAnswer);
                String correctAnswer = convertSymbolToNumber(req.getCorrect().trim());
                log.info(correctAnswer);
                return userAnswer.equals(correctAnswer);
            }
        }
        return false;
    }
    private String convertSymbolToNumber(String symbol) {
        switch (symbol) {
            case "①":
                return "1";
            case "②":
                return "2";
            case "③":
                return "3";
            case "④":
                return "4";
            case "⑤":
                return "5";
            case "⑥":
                return "6";
            default:
                return symbol;
        }
    }
//    private boolean checkAnswer(List<DailyQuestion> dailyQuestions,String cacheKey,String userId,DailyCheckDTO req){
//        for (DailyQuestion dailyQuestion : dailyQuestions) {
//            if (dailyQuestion.getQuestion().equals(req.getMatter())){
//                if (dailyQuestion.getAnswer().equals(req.getAnswer())){
//                    redisTemplate.opsForValue().set(cacheKey,cacheKey+":"+userId,1, TimeUnit.DAYS);
//                    return  true;
//                }
//            }
//        }
//        redisTemplate.opsForValue().set(cacheKey,userId,1, TimeUnit.DAYS);
//        return  false;
//    }
//    private void cacheData(List<DailyQuestion> questions,String  category) {
//        if (category.equals("일반행정")) {
//            try {
//                String jsonData = objectMapper.writeValueAsString(questions);
//                redisTemplate.opsForValue().set(CIVIL_CACHE_KEY, jsonData, 1, TimeUnit.DAYS);
//                return;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } if (category.equals("토익")){
//            try {
//                String jsonData = objectMapper.writeValueAsString(questions);
//                redisTemplate.opsForValue().set(TOEIC_CACHE_KEY, jsonData, 1, TimeUnit.DAYS);
//                return;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            String jsonData = objectMapper.writeValueAsString(questions);
//            redisTemplate.opsForValue().set(POLICE_CACHE_KEY, jsonData, 1, TimeUnit.DAYS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private void cacheData(List<DailyQuestion> questions) {
//        try {
//            String jsonData = objectMapper.writeValueAsString(questions);
//            redisTemplate.opsForValue().set(TOEIC_CACHE_KEY, jsonData, 1, TimeUnit.DAYS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
