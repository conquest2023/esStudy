package es.board.repository.entity.repository.infrastructure.jpa;

import es.board.repository.entity.poll.PollEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PollJpaRepository extends JpaRepository<PollEntity,Integer> {


//    @Query("in")


    @Query("select p from PollEntity p where p.post.id=:postId")
    Optional<PollEntity> findByPostId(@Param("postId") int postId);


    @Query("select p from PollEntity p where p.id=:pollId")
    Optional<PollEntity> findByPollId(@Param("pollId") long pollId);

    @Query("""
    select distinct poll
    from PollEntity poll
    join fetch poll.post p
    left join fetch poll.options opt
    where p.id = :postId
    """)
    PollEntity findPollDetail(@Param("postId") int postId);

}
