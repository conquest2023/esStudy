package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.interview.InterviewQuestionDTO;
import es.board.controller.model.dto.interview.InterviewAnswerDTO;
import es.board.controller.model.dto.interview.InterviewLogDTO;
import es.board.ex.TokenValidator;
import es.board.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")

public class InterviewQuestionController {


    private  static   final String  ANSWER_CACHE_KEY =  "answer_interview_question";

    private  final RedisTemplate redisTemplate;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final TokenValidator tokenValidator;


    private  final InterviewService interviewService;



    @PostMapping("/interview/log")
    public  void saveInterviewLog( @RequestHeader(value = "Authorization") String token,
                                   @RequestBody InterviewLogDTO logDTO){
        interviewService.saveLog(token,logDTO);
    }

    @GetMapping("/interview/random")
    public ResponseEntity<?> getRandomQuestion() {
        return ResponseEntity.ok(Map.of("question", interviewService.getRandomQuestions()));
    }
    @GetMapping("/interview/aggregation/{subCategory}")
    public ResponseEntity<?> getAggregationQuestion(@PathVariable String  subCategory) {
        return ResponseEntity.ok(Map.of(
                "aggregation", interviewService.getAggregationQuestion(subCategory)));
    }

    @GetMapping("/search/interview/{text}")
    public ResponseEntity<?> getSearchInterviewQuestion(@PathVariable String text) {
        return ResponseEntity.ok(Map.of("searchResult",
                interviewService.getSearchInterviewQuestion(text)));
    }


    @GetMapping("/interview/test")
    public ResponseEntity<?> getRandomTestQuestion() {
        List<InterviewQuestionDTO> questionDTO = interviewService.getRandomQuestions();
        if (questionDTO == null) {
            return ResponseEntity.ok(Map.of("message", "오늘의 질문을 찾을 수 없습니다."));
        }
        return ResponseEntity.ok(questionDTO);
    }

    @GetMapping("/interview/best/answer")
    public ResponseEntity<?> getBestAnswer(@RequestHeader("X-Question-Ids") List<String> questionIds) {
        List<InterviewAnswerDTO> answer = interviewService.getBestAnswer(questionIds);
        if (answer == null) {
            return ResponseEntity.ok(Map.of("message", "베스트 대답을 찾을 수 없습니다."));
        }
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/save/interview/question")
    public ResponseEntity<?> saveInterviewQuestion(@RequestBody InterviewAnswerDTO dto, @RequestHeader(value = "Authorization") String token) {

        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse.getStatusCode() != HttpStatus.OK) {
            return tokenCheckResponse;
        }
        Object body = tokenCheckResponse.getBody();
        if (body instanceof Map<?, ?> mapBody) {
            String rawToken = (String) mapBody.get("token");
            interviewService.saveQuestion(dto, jwtTokenProvider.getUserId(rawToken), jwtTokenProvider.getUsername(rawToken));
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "답변이 성공적으로 저장되었습니다."));
    }

    @GetMapping("/interview/check")
    public ResponseEntity<?> checkQuestionStatus(@RequestHeader(value = "Authorization") String token) {

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
        String userId = jwtTokenProvider.getUserId(token);
        String itKey = "IT_" + ANSWER_CACHE_KEY + "_" + userId;
        String generalKey = "GENERAL_" + ANSWER_CACHE_KEY + "_" + userId;
        int itCount = redisTemplate.opsForValue().get(itKey) == null ? 0 : (int) redisTemplate.opsForValue().get(itKey);
        int generalCount = redisTemplate.opsForValue().get(generalKey) == null ? 0 : (int) redisTemplate.opsForValue().get(generalKey);
        return ResponseEntity.ok(Map.of(
                "IT_count", itCount,
                "GENERAL_count", generalCount
        ));

    }

    public  boolean checkedToken(String token) {
        if (token == null) {
            return false;
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return false;
        }
        return true;
    }

}
