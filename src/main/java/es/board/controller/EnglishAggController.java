package es.board.controller;


import es.board.domain.english.EnglishAggService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnglishAggController {


    private final EnglishAggService englishAggService;

    @GetMapping("/count/{category}")
    public ResponseEntity<?> getTotalRcAttemptToday(@RequestAttribute("userId") String userId,
                                                    @PathVariable String category){


        int totalEnglishAttemptToday = englishAggService.getTotalEnglishAttemptToday(userId, category);
        return ResponseEntity.ok(Map.of("ok",totalEnglishAttemptToday));
    }
}
