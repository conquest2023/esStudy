package es.board.mapper;

import es.board.controller.model.dto.todo.TodoDTO;
import es.board.domain.todo.Todo;
import es.board.infrastructure.entity.todo.TodoEntity;
import es.board.repository.entity.TodoStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TodoMapper {

    public static TodoEntity toEntity(String userId, TodoDTO.Request request) {
        return TodoEntity.builder()
                .userId(userId)
                .title(request.getTitle())
                .priority(request.getPriority())
                .category(request.getCategory())
                .status(TodoStatus.IN_PROGRESS)
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Todo fromTodo(TodoEntity entity) {
        return Todo.builder()
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .priority(entity.getPriority())
                .category(entity.getCategory())
                .status(entity.getStatus())
                .dueDate(entity.getCreatedAt().toLocalDate().plusDays(1))
                .description(entity.getDescription())
                .createdAt(LocalDateTime.now())
//                .end(entity.getEnd() != null ? entity.getEnd() : LocalDate.now())
                .build();
    }

    public static List<TodoDTO.Response> toTodoList(List<TodoEntity> entities) {
        return entities.stream()
                .map(todo -> TodoDTO.Response.builder()
                        .todo_id(todo.getTodoId())
                        .title(todo.getTitle())
                        .description(todo.getDescription())
                        .category(todo.getCategory())
                        .priority(todo.getPriority())
                        .status(todo.getStatus())
                        .createdAt(todo.getCreatedAt())
                        .dueDate(todo.getDueDate())
                        .build())
                .collect(Collectors.toList());
    }

}
