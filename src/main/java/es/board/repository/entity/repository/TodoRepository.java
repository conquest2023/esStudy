package es.board.repository.entity.repository;

import es.board.infrastructure.entity.todo.TodoEntity;
import es.board.repository.entity.TodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Repository
public interface TodoRepository  extends JpaRepository<TodoEntity,Long> {


    @Query(" select p from TodoEntity  p where p.userId=:userId")
    List<TodoEntity> findTodosByUserId(@Param("userId") String userId);


    @Modifying
    @Transactional
    @Query("UPDATE TodoEntity u SET u.status = :status WHERE u.todoId = :todo_id")
    void updateStatus(@Param("status") TodoStatus status, @Param("todo_id") Long todo_id);

    @Query("SELECT count(*) FROM TodoEntity t WHERE t.userId = :userId AND DATE(t.end) = :today AND t.status = 'DONE'" )
    Long countByUserIdClearTodo(@Param("userId") String userId, @Param("today") LocalDate today);

    @Query("SELECT count(*) FROM TodoEntity t WHERE t.userId = :userId AND DATE(t.end) = :today")
    Long countGraphByUserAllId(@Param("userId") String userId, @Param("today") LocalDate today);


    @Query("SELECT t FROM TodoEntity t WHERE t.userId = :userId AND DATE(t.end) = :today AND t.project IS NULL ")
    List<TodoEntity> findTodayTodos(@Param("userId") String userId, @Param("today") LocalDate today);

    @Query("SELECT t FROM TodoEntity t WHERE t.userId = :userId")
    List<TodoEntity> findAllByTodos(@Param("userId") String userId);


    @Query("SELECT t FROM TodoEntity t WHERE t.userId = :userId AND t.project =true")
    List<TodoEntity> findProjectTodo(@Param("userId") String userId);

    @Query("SELECT count(*) FROM TodoEntity t WHERE t.userId = :userId AND t.status = 'IN_PROGRESS' AND DATE(t.end) = :today")
    Long countByUserIdAndStatusYetTodo(@Param("userId") String userId, LocalDate today);


    @Query("SELECT t.userId FROM  TodoEntity t  WHERE  t.status= 'IN_PROGRESS'" )
    List<String> findAllUserIds();

    @Query("SELECT t.userId FROM  TodoEntity t WHERE DATE(t.createdAt) = :today")
    List<String> findAllTodoUserTodayIds(LocalDate today);

    @Query("SELECT t.userId FROM  TodoEntity t WHERE DATE(t.end) = :today")
    Set<String> findSETAllTodoUserTodayIds(LocalDate today);
}
