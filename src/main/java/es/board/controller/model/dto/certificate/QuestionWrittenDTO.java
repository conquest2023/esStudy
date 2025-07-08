package es.board.controller.model.dto.certificate;

import es.board.controller.model.dto.certificate.ChoiceWrittenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionWrittenDTO {
    private Long questionId;
    private String category;
    private String type;
    private String questionText;
    private Integer difficulty;
    private List<ChoiceWrittenDto> choices;
}
