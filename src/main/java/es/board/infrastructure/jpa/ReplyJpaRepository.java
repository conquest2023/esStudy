package es.board.infrastructure.jpa;

import es.board.infrastructure.feed.ReplyAggView;
import es.board.infrastructure.entity.feed.ReplyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyJpaRepository extends JpaRepository<ReplyEntity,Long> {

    Page<ReplyEntity> findAll(Pageable pageable);


    @Query("select r from ReplyEntity r where r.postId=:id")
    List<ReplyEntity> findReplys(long id);

    @Query("""
      select r.postId as postId, count(r) as cnt
      from ReplyEntity r
      where r.postId in :postIds
      group by r.postId
      """)
    List<ReplyAggView> countRepliesIn(@Param("postIds") List<Integer> postIds);
}
