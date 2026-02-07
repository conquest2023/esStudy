package es.board.domain.todo;

import es.board.controller.model.dto.todo.DayOfWeekType;
import es.board.controller.model.dto.todo.RepeatType;
import es.board.infrastructure.entity.todo.TodoEntity;
import es.board.infrastructure.entity.todo.TodoRecurrenceWeekdayEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoRecurrence {


    private Long recurrenceId;

    private RepeatType repeatType;


    private Integer intervalValue;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


    private LocalDate repeatEndDate;


    private Set<TodoRecurrenceWeekdayEntity> weekdays = new HashSet<>();


    public TodoRecurrence of(Todo todo){

        return  TodoRecurrence.builder()
                .repeatType(todo.getRepeatType())
                .createdAt(LocalDateTime.now())
                .repeatEndDate(todo.getDueDate().toLocalDate())
                .build();
    }

}
