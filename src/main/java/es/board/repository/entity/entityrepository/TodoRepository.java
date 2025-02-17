package es.board.repository.entity.entityrepository;

import es.board.repository.entity.Todo;
import es.board.repository.entity.TodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TodoRepository  extends JpaRepository<Todo,Long> {


    @Query(" select p from Todo  p where p.userId=:userId")
    List<Todo> findTodosByUserId(@Param("userId") String userId);


    @Modifying
    @Transactional
    @Query("UPDATE Todo u SET u.status = :status WHERE u.todo_id = :todo_id")
    void updateStatus(@Param("status") TodoStatus status, @Param("todo_id") Long todo_id);


}
