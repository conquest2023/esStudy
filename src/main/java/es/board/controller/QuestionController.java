package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.ex.TokenValidator;
import es.board.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")

@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    private  final TokenValidator tokenValidator;

    private  final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/practical")
    @ResponseBody
    public ResponseEntity<?> getPractical(@RequestHeader(value = "Authorization", required = false) String token,
                                         @RequestParam String  category,
                                         @RequestParam(required = false) String  round) {
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse != null) {
            return tokenCheckResponse;
        }
        return ResponseEntity.ok(Map.of("DTOS",questionService.getQuestionPracticalList(category,round)));
    }


    @GetMapping("/practical/random")
    @ResponseBody
    public ResponseEntity<?> getRandomPractical(@RequestHeader(value = "Authorization", required = false) String token) {
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse != null) {
            return tokenCheckResponse;
        }
        return ResponseEntity.ok(Map.of("DTOS",questionService.getRandomPracticalList()));
    }


    @GetMapping("/practical/tag")
    @ResponseBody
    public ResponseEntity<?> getTagPractical(@RequestHeader(value = "Authorization", required = false) String token,
                                              @RequestParam Long  tagId) {
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse != null) {
            return tokenCheckResponse;
        }
        return ResponseEntity.ok(Map.of("DTOS",questionService.getTagPractical(tagId)));
    }
}
