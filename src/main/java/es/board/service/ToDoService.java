package es.board.service;

import es.board.controller.model.dto.todo.D_DayDTO;
import es.board.controller.model.dto.todo.TodoDTO;

import java.util.List;

public interface ToDoService {

    List<TodoDTO.Request> getProjectTodo(String userId);
    List<TodoDTO.Request> getUserToDo(String userId);

    List<TodoDTO.Request> getUserAllToDo(String userId);

    void addToDo(String token, TodoDTO.Response todoResponse);


    void addD_Day(String token, D_DayDTO dDayDTO);

    List<D_DayDTO> getD_Day(String  token);

    Object getRemainingTodos(String userId);
    void  deleteToDo(Long id,String token);

    void updateTodoCache(String userId);


    void delete_D_Day(Long id);
    void completeTodo(String token, Long id);

    Long getDoneTodo(String userId);

    void addProjectTodo(String userId, TodoDTO.Response todoResponse, String username);

}
