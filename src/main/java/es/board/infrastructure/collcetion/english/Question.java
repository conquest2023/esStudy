package es.board.infrastructure.collcetion.english;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
public class Question {

//    private int qIndex;
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private String explanation;
}
