package es.board.repository.entity.repository;

import es.board.repository.entity.feed.NoticeEntity;
import es.board.repository.entity.feed.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface NoticeJpaRepository extends JpaRepository<NoticeEntity, Long> {


    @Query("SELECT p FROM NoticeEntity u INNER JOIN PostEntity p ON u.post.id = p.id WHERE p.id = :id")
    PostEntity findDetailNotice(@Param("id") Long id);
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM NoticeEntity n WHERE n.userId = :userId")
    boolean existsByUserId(@Param("userId") String userId);


    @Query("SELECT p from  NoticeEntity  u " +
            " inner join PostEntity p on u.post.id = p.id" +
            " order by u.createdAt DESC limit 1")
    PostEntity findNoticeByCreatedAtDESC();



    @Query("select p from PostEntity p where p.category=:category")
    Page<PostEntity> findByPageNotices(@Param("category") String category,Pageable pageable);
}