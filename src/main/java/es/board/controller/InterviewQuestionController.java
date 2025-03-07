package es.board.controller;

import es.board.repository.entity.InterviewQuestion;
import es.board.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InterviewQuestionController {


    private  final InterviewService interviewService;

    @GetMapping("/interview/random")
    public ResponseEntity<?> getRandomQuestion() {
        return ResponseEntity.ok(Map.of("question", interviewService.getTodayQuestion()));
    }
}
