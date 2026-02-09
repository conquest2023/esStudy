package es.board.infrastructure.english.collcetion;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
public class ProblemContent {

    private String passage;
    private String word;
    private List<Question> questions;
}
