package es.board.controller;


import es.board.domain.english.EnglishService;
import es.board.infrastructure.collcetion.english.EnglishProblem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnglishController {

    private final EnglishService englishService;



    @GetMapping("/english")
    public ResponseEntity<?> getProblems(
            @RequestParam(required = false) String lastId,
            @RequestParam(defaultValue = "10") int size) {

        List<EnglishProblem> problems = englishService.getRandomProblems(size);

        return ResponseEntity.ok(
                Map.of("ok",problems));
    }
}
