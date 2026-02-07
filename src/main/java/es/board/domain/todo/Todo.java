package es.board.domain.todo;

import es.board.controller.model.dto.todo.DayOfWeekType;
import es.board.controller.model.dto.todo.RepeatType;
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
import java.util.List;

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

    private RepeatType repeatType;

    private List<DayOfWeekType> repeatDays;

    private TodoStatus status;

    private Integer priority;

    private TodoRecurrence recurrence;

    private LocalDateTime dueDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDate repeatEndDate;


    public void calculateRepeat(Todo todo, RepeatType repeatType){
            if (repeatType!=null){
                recurrence.of(todo);
            }
    }
}
