package es.board.controller.model.res;

import es.board.repository.entity.TodoStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoResponse {

    private String userId;          // 해당 Todo가 어느 사용자의 것인지

    private String title;

    private String description;

    private  String category;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;         // 마감일

    @Enumerated(EnumType.STRING)
    private TodoStatus status;    // 예: TODO, IN_PROGRESS, DONE

    private Integer priority;     // 우선순위 (1 높음, 5 낮음 등)

    private LocalDateTime createdAt;

}
