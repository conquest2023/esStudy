package es.board.controller;


import es.board.domain.english.EnglishService;
import es.board.infrastructure.english.collcetion.English_RC;
import es.board.infrastructure.english.collcetion.English_Vocab;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnglishProblemController {

    private final EnglishService englishService;



    @GetMapping("/english")
    public ResponseEntity<?> getProblems(
            @RequestParam(required = false) String lastId,
            @RequestParam(defaultValue = "10") int size) {

        List<English_RC> problems = englishService.getRandomProblems(size);

        return ResponseEntity.ok(
                Map.of("ok",problems));
    }



    @GetMapping("/vocab")
    public ResponseEntity<?> getVocab(
            @RequestParam(required = false) String lastId,
            @RequestParam(defaultValue = "10") int size) {

        List<English_Vocab> problems = englishService.getRandomVocab(size);
        return ResponseEntity.ok(
                Map.of("ok",problems));
    }

    @GetMapping("/vocab/{level}")
    public ResponseEntity<?> getVocabLevel(
            @PathVariable String level,
            @RequestParam(defaultValue = "10") int size) {

        List<English_Vocab> problems = englishService.getVocabLevel(level.toUpperCase(),size);
        return ResponseEntity.ok(
                Map.of("ok",problems));
    }
}
