package es.board.service;

import es.board.controller.model.req.D_DayRequest;
import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.TodoResponse;

import java.util.List;

public interface ToDoService {

    List<TodoRequest> getProjectTodo(String userId);
    List<TodoRequest> getUserToDo(String userId);

    List<TodoRequest> getUserAllToDo(String userId);

    void addToDo(String token, TodoResponse todoResponse);


    void addD_Day(String token, D_DayRequest dDayDTO);

    List<D_DayRequest> getD_Day(String  token);

    Object getRemainingTodos(String userId);
    void  deleteToDo(Long id,String token);

    void updateTodoCache(String userId) ;


    void delete_D_Day(Long id);
    void completeTodo(String token, Long id);

    Long getDoneTodo(String userId);

    void addProjectTodo(String userId, TodoResponse todoResponse,String username);

}
