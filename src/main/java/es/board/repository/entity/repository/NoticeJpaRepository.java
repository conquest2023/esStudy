package es.board.repository.entity.repository;

import es.board.repository.entity.feed.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface NoticeJpaRepository extends JpaRepository<NoticeEntity, Long> {


    @Query("select u from NoticeEntity u where u.id = :id")
    NoticeEntity findDetailNotice(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM NoticeEntity n WHERE n.userId = :userId")
    boolean existsByUserId(@Param("userId") String userId);


    @Query("SELECT u from  NoticeEntity  u order by u.createdAt DESC limit 1")
    NoticeEntity findNoticeByCreatedAtDESC();
}