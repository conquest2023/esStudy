package es.board.infrastructure.jpa;

import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.entity.poll.PollEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PollJpaRepository extends JpaRepository<PollEntity,Integer> {


    @Query(
            value = "select p.id from post p " +
                    " inner join poll po on po.post_id = p.id" +
                    " order by p.created_at desc limit :size offset :offset",
            nativeQuery = true
    )
    List<Integer> findPollIds(@Param("offset") int offset,
                              @Param("size") int size);

    @Query("select po from PollEntity p " +
            " inner join PostEntity po on p.post.id=po.id" +
            " where p.id=:pollId")
    Optional<PostEntity> findByPost(@Param("pollId") long pollId);

    @Query("select p from PollEntity p where p.post.id=:postId")
    Optional<PollEntity> findByPostId(@Param("postId") int postId);

    @Query("select p from PollEntity po inner join PostEntity p on po.post.id =p.id")
    Page<PostEntity> findByPollPagingList(Pageable pageable);

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
