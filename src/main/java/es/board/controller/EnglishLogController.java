package es.board.controller;


import es.board.controller.model.dto.english.EnglishProblemAttemptDto;
import es.board.controller.model.dto.english.WrongNoteDto;
import es.board.domain.english.EnglishLogService;
import es.board.infrastructure.english.entity.WrongNote;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnglishLogController {

    private final EnglishLogService englishService;



    @PostMapping("/wrongnote")
    public ResponseEntity<?> saveWrongNote(@RequestAttribute(required = true)
            String  userId, @RequestBody WrongNoteDto.Request request) {

        englishService.saveWrongNote(userId,request);
        return ResponseEntity.ok("ok");
    }


    @PostMapping("/english/log")
    public ResponseEntity<?> saveEnglishLog(@RequestAttribute(required = true) String  userId,
                                            @RequestBody EnglishProblemAttemptDto.Request request) {
        englishService.saveEnglishLog(userId,request);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/wrong-note/{category}")
    public ResponseEntity<?> getWrongNote(@RequestAttribute(required = true) String  userId,
                                          @PathVariable String category,
                                          @RequestParam int page,
                                          @RequestParam int size) {
        List<?> wrongNoteList = englishService.getWrongNoteList(page, size, category, userId);

        return ResponseEntity.ok(Map.of("ok",wrongNoteList));
    }
}
