package es.board.repository.entity.repository;

import es.board.repository.entity.feed.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository  extends JpaRepository<PostEntity,Integer> {



    @Modifying
    @Query("delete  from PostEntity p where  p.id=:id and p.userId=:userId")
    void deleteById(@Param("id") String id , @Param("userId") String userId);



    @Query(" select p.userId from PostEntity p where p.feedUID=:id")
    String findByFeedUID(@Param("id") String id);
}
