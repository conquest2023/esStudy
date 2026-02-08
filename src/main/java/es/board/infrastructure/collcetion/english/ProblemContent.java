package es.board.infrastructure.collcetion.english;

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
