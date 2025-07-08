package es.board.controller.model.dto.certificate;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionPracticalDTO {

    private Long questionId;
    private String category;
    private String type;
    private String questionText;
    private String modelAnswer;
    private String explanation;
    private Integer difficulty;
}
