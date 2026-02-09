package es.board.infrastructure.english.collcetion;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
public class Question {

//    private int qIndex;
    private String text;
    private List<String> options;
    private String answer;
    private String explanation;
}
