package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.res.DailyBookMark;
import es.board.repository.entity.DailyQuestion;
import es.board.service.DailyQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DailyQuestionController {


    private  final JwtTokenProvider jwtTokenProvider;

    private  final DailyQuestionService dailyQuestionService;


    @GetMapping("/toeic")
    public ResponseEntity<?> getToeicDailyQuestion(@RequestParam String  category){
        List<DailyQuestion> toeic = dailyQuestionService.findToeicDailyQuestion(category);
        return  ResponseEntity.ok(Map.of(
                "toeic",toeic));
    }


    @GetMapping("/civil")
    public ResponseEntity<?> getCivilDailyQuestion(@RequestParam  String  category){
        List<DailyQuestion> civil = dailyQuestionService.findCivilDailyQuestion(category);
        return  ResponseEntity.ok(Map.of(
                "civil",civil));
    }

    @GetMapping("/police")
    public ResponseEntity<?> getPoliceDailyQuestion(@RequestParam  String  category){
        List<DailyQuestion> police = dailyQuestionService.findPoliceDailyQuestion(category);
        return  ResponseEntity.ok(Map.of(
                "police",police));
    }

    @PostMapping("/daily/bookmark")
    public  ResponseEntity<?> saveDailyBookmark(@RequestHeader(value = "Authorization", required = false) String token
            , @RequestBody DailyBookMark daily){
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
         dailyQuestionService.saveDailyBookMark(jwtTokenProvider.getUserId(token),daily);
         return  ResponseEntity.ok("ok");
    }
    @GetMapping("/get/daily/bookmark")
    public  ResponseEntity<?> getDailyBookmark(@RequestHeader(value = "Authorization", required = false) String token){
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
        List<DailyQuestion> questions=dailyQuestionService.getDailyQuestion(jwtTokenProvider.getUserId(token));
        return ResponseEntity.ok(Map.of("bookmarks", questions));
    }
}
