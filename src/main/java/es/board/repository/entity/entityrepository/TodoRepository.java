package es.board.repository.entity.entityrepository;

import es.board.repository.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TodoRepository  extends JpaRepository<Todo,Long> {


    @Query(" select p from Todo  p where p.userId=:userId")
    List<Todo> findTodosByUserId(@Param("userId") String userId);

}
