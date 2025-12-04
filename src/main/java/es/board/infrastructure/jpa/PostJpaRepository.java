package es.board.infrastructure.jpa;

import es.board.infrastructure.entity.feed.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostJpaRepository  extends JpaRepository<PostEntity,Integer> {


    @Query("""
        select p
        from PostEntity p
        where not exists (select 1 from NoticeEntity n where n.post = p)
        order by p.createdAt desc
        """)
    Page<PostEntity> findPostPagingList(Pageable pageable);



    @Query("select p.id from PostEntity p")
    Page<Integer> findIds(Pageable pageable);

    @Query( value = "select p.id from post p " +
                    " where not exists (select 1 from notice n where n.post_id = p.id)" +
                    " order by p.created_at desc limit :size offset :offset",
            nativeQuery = true)
    List<Integer> findPostIds(@Param("offset") int offset,
                              @Param("size") int size);

    @Query( value = "select p.id from post p" +
            " where p.created_at>=:day  " +
                    " order by p.view_count desc , p.id asc limit :size offset :offset",
            nativeQuery = true)
    List<Integer> findBestPostIds(@Param("offset") int offset,
                                  @Param("size") int size,
                                  @Param("day") LocalDateTime day);
    @Query("select p from PostEntity p where p.userId=:userId")
    Page<PostEntity> findByMyPageUserPosts(Pageable pageable,String userId);


    @Query("select p.userId from PostEntity p where p.id=:postId")
    String findByUserId(@Param("postId") int postId);

    @Query("select  p from PostEntity  p where p.createdAt>=:sevenDaysAgo order by p.viewCount DESC , p.id ASC ")
    Page<PostEntity> findPopularPostsInLast7Week(Pageable pageable, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);



    @Query("select  p from PostEntity  p where p.createdAt>=:lastMonth order by p.viewCount DESC , p.id ASC ")
    Page<PostEntity> findPopularMonthPosts(Pageable pageable, @Param("lastMonth") LocalDateTime lastMonth);

    @Query("select  p from PostEntity  p " +
            " where p.createdAt>=:today " +
            " order by p.viewCount DESC , p.id ASC ")
    Page<PostEntity> findPopularTodayPosts(Pageable pageable, @Param("today") LocalDateTime today);

    @Query(" select p from PostEntity p where p.createdAt>= :today order by p.viewCount desc limit 3")
    List<PostEntity> findTodayTop3Native(@Param("today") LocalDateTime today);



    @Query("""
         select p
          from PostEntity p
          where p.userId = :userId and p.createdAt >= :lastDay
          order by p.viewCount desc limit 1
        """)
    Optional<PostEntity> findUserTopToday(@Param("userId") String userId,
                                          @Param("lastDay") LocalDateTime lastDay);

    @Modifying
    @Query("update PostEntity p set p.viewCount = p.viewCount + 1 where p.id = :postId")
    void incrementViewCount(@Param("postId") int postId);


}
