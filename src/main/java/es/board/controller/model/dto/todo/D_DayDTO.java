package es.board.controller.model.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class D_DayDTO {

    private Long id;
    private String  userId;
    private String category;
    private String examName;
    private LocalDate examDate;
    private String goal;
    private Integer progress;

    private long dDay;

    private LocalDateTime createdAt;

}
