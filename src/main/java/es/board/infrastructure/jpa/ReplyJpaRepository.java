package es.board.infrastructure.jpa;

import es.board.infrastructure.feed.ReplyAggView;
import es.board.infrastructure.entity.feed.ReplyEntity;
import es.board.infrastructure.jpa.projection.PostWithReplyCount;
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

    @Query("SELECT p.id as id, " +
            "p.title as title, " +
            "p.username as username, " +
            "p.description as description, " +
            "p.viewCount as viewCount, " +
            "p.createdAt as createdAt, " +
            "count(c.postId) as replyCount " + // 필드 이름 일치 중요
            "FROM PostEntity p " +
            "LEFT JOIN ReplyEntity c ON p.id = c.postId " +
            "GROUP BY p.id, p.title, p.username, p.description, p.viewCount, p.createdAt " +
            "ORDER BY replyCount DESC")
    Page<PostWithReplyCount> findByReplyCountDESC(Pageable pageable);
}
