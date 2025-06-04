package es.board.controller;


import es.board.repository.entity.DailyQuestion;
import es.board.service.DailyQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/daliy/bookmark")
    public  ResponseEntity<?> saveDailyBookmark(){
        return  null;
    }
}
