package es.board.controller.model.dto.certificate;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoiceWrittenDto {

    private Long choiceId;
    private String choiceText;
    private Boolean isCorrect;
}
