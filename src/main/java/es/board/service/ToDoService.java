package es.board.service;

import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.TodoResponse;

import java.util.List;

public interface ToDoService {


    List<TodoRequest> getUserToDo(String userId);

    void saveUserToDo(String token, TodoResponse todoResponse);

}
