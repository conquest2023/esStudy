package es.board.controller;


import es.board.domain.english.EnglishService;
import es.board.infrastructure.collcetion.english.EnglishProblem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EnglishController {

    private final EnglishService englishService;



    @GetMapping("/english")
    public ResponseEntity<?> getProblemList(){
        List<EnglishProblem> problemList = englishService.findProblemList();

        return ResponseEntity.ok(problemList);
    }

}
