package es.board.controller.model.req;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionPracticalDto {

    private Long questionId;
    private String category;
    private String type;
    private String questionText;
    private String modelAnswer;
    private String explanation;
    private Integer difficulty;
}
