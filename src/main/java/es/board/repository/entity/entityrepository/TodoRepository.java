package es.board.repository.entity.entityrepository;

import es.board.repository.entity.Todo;
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
public interface TodoRepository  extends JpaRepository<Todo,Long> {


    @Query(" select p from Todo  p where p.userId=:userId")
    List<Todo> findTodosByUserId(@Param("userId") String userId);


    @Modifying
    @Transactional
    @Query("UPDATE Todo u SET u.status = :status WHERE u.todo_id = :todo_id")
    void updateStatus(@Param("status") TodoStatus status, @Param("todo_id") Long todo_id);

    @Query("SELECT count(*) FROM Todo t WHERE t.userId = :userId AND DATE(t.end) = :today AND t.status = 'DONE'" )
    Long countByUserIdClearTodo(@Param("userId") String userId, @Param("today") LocalDate today);

    @Query("SELECT count(*) FROM Todo t WHERE t.userId = :userId AND DATE(t.end) = :today")
    Long countGraphByUserAllId(@Param("userId") String userId, @Param("today") LocalDate today);


    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND DATE(t.end) = :today AND t.project IS NULL ")
    List<Todo> findTodayTodos(@Param("userId") String userId, @Param("today") LocalDate today);

    @Query("SELECT t FROM Todo t WHERE t.userId = :userId")
    List<Todo> findAllByTodos(@Param("userId") String userId);


    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.project =true")
    List<Todo> findProjectTodo(@Param("userId") String userId);

    @Query("SELECT count(*) FROM Todo t WHERE t.userId = :userId AND t.status = 'IN_PROGRESS' AND DATE(t.end) = :today")
    Long countByUserIdAndStatusYetTodo(@Param("userId") String userId, LocalDate today);


    @Query("SELECT t.userId FROM  Todo t  WHERE  t.status= 'IN_PROGRESS'" )
    List<String> findAllUserIds();

    @Query("SELECT t.userId FROM  Todo t WHERE DATE(t.createdAt) = :today")
    List<String> findAllTodoUserTodayIds(LocalDate today);

    @Query("SELECT t.userId FROM  Todo t WHERE DATE(t.end) = :today")
    Set<String> findSETAllTodoUserTodayIds(LocalDate today);
}
