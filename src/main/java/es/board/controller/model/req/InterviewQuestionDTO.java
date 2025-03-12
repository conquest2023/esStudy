package es.board.controller.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class InterviewQuestionDTO {


    private Long id;

    private String category;
    private String question;
    private LocalDateTime createdAt;
}
