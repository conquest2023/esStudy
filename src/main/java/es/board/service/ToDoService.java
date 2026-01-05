package es.board.service;

import es.board.controller.model.dto.todo.TodoDTO;

import java.util.List;

public interface ToDoService {

    List<TodoDTO.Response> getProjectTodo(String userId);
    List<TodoDTO.Response> getTodayTodos(String userId);

    List<TodoDTO.Response> getAllTodos(String userId);

    void addTodo(String userId, TodoDTO.Request request);


    Object getRemainingTodos(String userId);
    void deleteTodo(Long id, String  userId);

    void updateTodoCache(String userId);


    void finishTodo(String userId, Long id);

    Long getDoneTodo(String userId);

    void addProjectTodo(String userId, TodoDTO.Request todoResponse, String username);

}
