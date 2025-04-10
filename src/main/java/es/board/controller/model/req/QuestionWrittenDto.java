package es.board.controller.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionWrittenDto {
    private Long questionId;
    private String category;
    private String type;
    private String questionText;
    private Integer difficulty;
    private List<ChoiceWrittenDto> choices;
}
