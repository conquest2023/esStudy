package es.board.repository.entity.entityrepository;

import es.board.repository.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository  extends JpaRepository<Post,Integer> {



    @Modifying
    @Query("delete  from Post p where p.userId=:userId")
    void deleteById(@Param("userId") String userId);

}
