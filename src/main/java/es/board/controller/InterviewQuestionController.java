package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.req.InterviewQuestionDTO;
import es.board.controller.model.res.InterviewAnswerDTO;
import es.board.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InterviewQuestionController {


    private  final InterviewService interviewService;

    private  final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/interview/random")
    public ResponseEntity<?> getRandomQuestion() {
        return ResponseEntity.ok(Map.of("question", interviewService.getTodayQuestion()));
    }


    @GetMapping("/interview/test")
    public ResponseEntity<?> getRandomTestQuestion() {
        List<InterviewQuestionDTO> questionDTO = interviewService.getRandomQuestions();
        if (questionDTO == null) {
            return ResponseEntity.ok(Map.of("message", "오늘의 질문을 찾을 수 없습니다."));
        }
        return ResponseEntity.ok(questionDTO);
    }

    @PostMapping("/save/interview/question")
    public ResponseEntity<?> getRepeatSchedule(@RequestBody InterviewAnswerDTO dto, @RequestHeader(value = "Authorization") String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }

        interviewService.saveQuestion(dto,jwtTokenProvider.getUserId(token));
        return ResponseEntity.ok(Map.of("success", true, "message", "답변이 성공적으로 저장되었습니다."));
    }
}
