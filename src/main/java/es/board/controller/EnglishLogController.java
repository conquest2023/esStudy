package es.board.controller;


import es.board.controller.model.dto.english.EnglishProblemAttemptDto;
import es.board.controller.model.dto.english.WrongNoteDto;
import es.board.domain.english.EnglishLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
