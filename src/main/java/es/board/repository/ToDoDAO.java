package es.board.repository;

import es.board.repository.document.Todo;

import java.util.List;

public interface ToDoDAO {

    void savePercentTodo(List<Todo> todo);
}
