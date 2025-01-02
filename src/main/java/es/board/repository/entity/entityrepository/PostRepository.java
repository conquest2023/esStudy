package es.board.repository.entity.entityrepository;

import es.board.repository.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository  extends JpaRepository<Post,Integer> {


}
