package es.board.controller;


import es.board.controller.model.dto.english.WrongNoteDto;
import es.board.domain.english.EnglishLogService;
import es.board.domain.english.EnglishService;
import es.board.infrastructure.english.collcetion.EnglishProblem;
import es.board.infrastructure.english.entity.WrongNote;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnglishLogController {

    private final EnglishLogService englishService;



    @PostMapping("/english/log")
    public ResponseEntity<?> getProblemsLog(@RequestAttribute(required = true)
            String  userId,
            @RequestBody WrongNoteDto.Request request) {

//        List<EnglishProblem> problems = englishService.getRandomProblems(size);

        englishService.saveWrongNote(userId,request);
        return ResponseEntity.ok("ok");
    }
}
