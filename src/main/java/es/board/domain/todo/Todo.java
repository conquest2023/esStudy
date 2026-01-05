package es.board.domain.todo;

import es.board.infrastructure.entity.todo.TodoRecurrenceEntity;
import es.board.repository.entity.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.StoreManager;
import org.xml.sax.helpers.AttributesImpl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    private Long todoId;

    private String userId;

    private String category;

    private String title;

    private String description;

    private TodoStatus status;

    private Integer priority;

    private Boolean project;

    private TodoRecurrenceEntity recurrence;

    private LocalDate dueDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDate end;


}
