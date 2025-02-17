package es.board.controller.model.mapper;


import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.TodoResponse;
import es.board.repository.document.Board;
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
                .status(TodoStatus.IN_PROGRESS)
                .description(todoResponse.getDescription())
                .dueDate(todoResponse.getDueDate())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public List<TodoRequest> EntityToTodo(List<Todo> todo) {
        return todo.stream()
                .map(todo1 -> TodoRequest.builder()
                        .userId(todo1.getUserId())
                        .title(todo1.getTitle())
                        .description(todo1.getDescription())
                        .priority(todo1.getPriority())
                        .status(todo1.getStatus())
                        .dueDate(todo1.getDueDate())
                        .build())
                .collect(Collectors.toList());
    }
}
