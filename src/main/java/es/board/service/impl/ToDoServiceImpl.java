package es.board.service.impl;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.ToDoMapper;
import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.TodoResponse;
import es.board.repository.entity.entityrepository.TodoRepository;
import es.board.service.ToDoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ToDoServiceImpl implements ToDoService {

    private  final TodoRepository todoRepository;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final ToDoMapper toDoMapper;

    @Override
    public List<TodoRequest> getUserToDo(String userId) {
        return toDoMapper.EntityToTodo(todoRepository.findTodosByUserId(userId));
    }

    @Override
    public void saveUserToDo(String token, TodoResponse todoResponse) {

        todoRepository.save(toDoMapper.TodoToEntity(jwtTokenProvider.getUserId(token), todoResponse));
    }
}
