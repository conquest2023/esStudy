package es.board.controller.model.mapper;


import es.board.controller.model.req.ScheduleDTO;
import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.TodoResponse;
import es.board.repository.entity.Schedule;
import es.board.repository.entity.Todo;
import es.board.repository.entity.TodoStatus;
import lombok.Data;
import java.util.List;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Data
@Component
public class ToDoMapper {


    public Todo TodoToEntity(String userId, TodoResponse todoResponse) {
        return Todo.builder()
                .userId(userId)
                .title(todoResponse.getTitle())
                .priority(todoResponse.getPriority())
                .category(todoResponse.getCategory())
                .status(TodoStatus.IN_PROGRESS)
                .description(todoResponse.getDescription())
                .dueDate(todoResponse.getDueDate())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public List<TodoRequest> EntityToTodo(List<Todo> todo) {
        return todo.stream()
                .map(todo1 -> TodoRequest.builder()
                        .todo_id(todo1.getTodo_id())
//                        .userId(todo1.getUserId())
                        .title(todo1.getTitle())
                        .description(todo1.getDescription())
                        .category(todo1.getCategory())
                        .priority(todo1.getPriority())
                        .status(todo1.getStatus())
                        .dueDate(todo1.getDueDate())
                        .createdAt(todo1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }


    public  List<ScheduleDTO> fromSchedule(List<Schedule> schedule) {
        return schedule.stream()
                .map(schedule1 -> ScheduleDTO.builder()
                        .scheduleId(schedule1.getScheduleId())
                        .userId(schedule1.getUserId())
                        .title(schedule1.getTitle())
                        .category(schedule1.getCategory())
                        .description(schedule1.getDescription())
                        .allDay(schedule1.getAllDay())
                        .location(schedule1.getLocation())
                        .startDatetime(schedule1.getStartDatetime())
                        .endDatetime(schedule1.getEndDatetime())
                        .build())
                .collect(Collectors.toList());
    }

    // DTO -> 엔터티 변환 메서드
    public Schedule toScheduleEntity(String userId, ScheduleDTO scheduleDTO) {
        return Schedule.builder()
                .userId(userId)
                .title(scheduleDTO.getTitle())
                .startDatetime(LocalDateTime.now())
                .category(scheduleDTO.getCategory())
                .endDatetime(scheduleDTO.getEndDatetime())
                .allDay(scheduleDTO.getAllDay())
                .location(scheduleDTO.getLocation())
                .description(scheduleDTO.getDescription())
                .build();
    }


    public es.board.repository.document.Schedule toScheduleDocument(String userId, ScheduleDTO scheduleDTO,Long id) {
        return es.board.repository.document.Schedule.builder()
                .scheduleId(id)
                .userId(userId)
                .title(scheduleDTO.getTitle())
                .startDatetime(LocalDateTime.now())
                .category(scheduleDTO.getCategory())
                .endDatetime(scheduleDTO.getEndDatetime())
                .allDay(scheduleDTO.getAllDay())
                .location(scheduleDTO.getLocation())
                .description(scheduleDTO.getDescription())
                .build();
    }


    public  List<ScheduleDTO> fromScheduleDocument(List<es.board.repository.document.Schedule> schedule) {
        return schedule.stream()
                .map(schedule1 -> ScheduleDTO.builder()
                        .scheduleId(schedule1.getScheduleId())
                        .userId(schedule1.getUserId())
                        .title(schedule1.getTitle())
                        .category(schedule1.getCategory())
                        .description(schedule1.getDescription())
                        .location(schedule1.getLocation())
                        .startDatetime(schedule1.getStartDatetime())
                        .endDatetime(schedule1.getEndDatetime())
                        .build())
                .collect(Collectors.toList());
    }
}
