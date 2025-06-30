package es.board.controller.model.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InterviewAnswerDTO {


    private Long id;

    private Long questionId;

    private String userId;

    private String  username;

    private String title;

    private String  category;

    private  String  subCategory;

    private String answer;

    private LocalDateTime createdAt;

}
