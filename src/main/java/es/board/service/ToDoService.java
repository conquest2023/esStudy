package es.board.service;

import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.TodoResponse;

import java.util.List;

public interface ToDoService {


    List<TodoRequest> getUserToDo(String userId);

    void addToDo(String token, TodoResponse todoResponse);


    Object getRemainingTodos(String userId);
    void  deleteToDo(Long id,String token);

    void updateTodoCache(String userId) ;

        void completeTodo(String token, Long id);

    Long getDoneTodo(String userId);

}
