package es.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.board.controller.record.NormalizeRequest;
import es.board.controller.record.NormalizedContent;
import es.board.domain.NormalizeService;
import es.board.infrastructure.es.ViewEventService;
import es.board.infrastructure.es.document.View;
import es.board.infrastructure.gemini.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class TestController {


    private final NormalizeService normalizeService;

    private final GeminiService geminiService;

    private final ViewEventService viewEventService;




//    @GetMapping("/gemini/test")
//    public ResponseEntity<?> testGemini() throws JsonProcessingException {
//        List<String> analysis = geminiService.getAnalysis();
//        return ResponseEntity.ok(Map.of("hi", analysis));
//    }





    @GetMapping("/views/test")
    public ResponseEntity<?> testUsers() {
        List<View> view = viewEventService.getUsersDailyViewHistorys(List.of(
               null
        ), LocalDateTime.now());
        return ResponseEntity.ok(Map.of("hi", view));
    }

}
