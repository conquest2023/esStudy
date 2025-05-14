package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.req.QuestionPracticalDto;
import es.board.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")

@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;


    private  final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/practical")
    @ResponseBody
    public ResponseEntity<?> getPractical(@RequestHeader(value = "Authorization", required = false) String token,
                                         @RequestParam String  category,
                                         @RequestParam(required = false) String  round) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }

        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }

        Map<String, Object> response = new HashMap<>();
        List<QuestionPracticalDto> certificateDTOS=questionService.getQuestionPracticalList(category,round);
        response.put("DTOS", certificateDTOS);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/practical/random")
    @ResponseBody
    public ResponseEntity<?> getRandomPractical(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }

        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }

        Map<String, Object> response = new HashMap<>();
        List<QuestionPracticalDto> certificateDTOS=questionService.getRandomPracticalList();
        response.put("DTOS", certificateDTOS);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/practical/tag")
    @ResponseBody
    public ResponseEntity<?> getTagPractical(@RequestHeader(value = "Authorization", required = false) String token,
                                              @RequestParam Long  tagId) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
        Map<String, Object> response = new HashMap<>();
        List<QuestionPracticalDto> certificateDTOS=questionService.getTagPractical(tagId);
        response.put("DTOS", certificateDTOS);
        return ResponseEntity.ok(response);
    }



}
