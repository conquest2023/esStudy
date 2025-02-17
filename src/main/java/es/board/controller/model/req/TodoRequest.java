package es.board.controller.model.req;

import es.board.repository.entity.TodoStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoRequest {

    private String userId;          // 해당 Todo가 어느 사용자의 것인지

    private String title;
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;         // 마감일

    @Enumerated(EnumType.STRING)
    private TodoStatus status;    // 예: TODO, IN_PROGRESS, DONE

    private Integer priority;     // 우선순위 (1 높음, 5 낮음 등)

    private Date createdAt;

    private Date updatedAt;
}
