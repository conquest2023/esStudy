package es.board.repository.entity.repository;

import es.board.repository.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {


    @Query("select u from Notice u where u.id = :id")
    Notice findByNoticeOne(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Notice n WHERE n.userId = :userId")
    boolean existsByUserId(@Param("userId") String userId);


    @Query("SELECT u from  Notice  u order by u.createdAt DESC limit 1")
    List<Notice> findNoticeByCreatedAtDESC();
}