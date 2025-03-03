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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoRequest {

    private Long todo_id;


    private String title;
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Enumerated(EnumType.STRING)
    private TodoStatus status;

    private  String category;

    private Integer priority;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
