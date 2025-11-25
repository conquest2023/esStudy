package es.board.repository.entity.repository.infrastructure.jpa;

import es.board.repository.entity.feed.PostEntity;
import es.board.repository.entity.repository.infrastructure.feed.CommentAggView;
import es.board.repository.entity.repository.infrastructure.feed.ReplyAggView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostJpaRepository  extends JpaRepository<PostEntity,Integer> {


    @Query("""
        select p
        from PostEntity p
        where not exists (select 1 from NoticeEntity n where n.post = p)
        order by p.createdAt desc
        """)
    Page<PostEntity> findAll(Pageable pageable);


    @Query("select p.id from PostEntity p")
    Page<Integer> findIds(Pageable pageable);

    @Query("select p.id from PostEntity p order by p.createdAt desc")
    List<Integer> findPostIds(Pageable pageable);


    @Query("select p from PostEntity p where p.userId=:userId")
    Page<PostEntity> findByMyPageUserPosts(Pageable pageable,String userId);


    @Query("select p.userId from PostEntity p where p.id=:postId")
    String findByUserId(@Param("postId") int postId);

    @Query("select  p from PostEntity  p where p.createdAt>=:sevenDaysAgo order by p.viewCount DESC ")
    Page<PostEntity> findPopularPostsInLast7Week(Pageable pageable, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

    @Modifying
    @Query("update PostEntity p set p.viewCount = p.viewCount + 1 where p.id = :postId")
    void incrementViewCount(@Param("postId") int postId);


    @Query("""
      select  c.postId as postId,
       count(c) as cnt
      from CommentEntity c
      where c.postId in :postIds
      group by c.postId
      """)
    List<CommentAggView> countCommentsIn(@Param("postIds") List<Integer> postIds);

    @Query("""
      select r.postId as postId, count(r) as cnt
      from ReplyEntity r
      where r.postId in :postIds
      group by r.postId
      """)
    List<ReplyAggView> countRepliesIn(@Param("postIds") List<Integer> postIds);
}
