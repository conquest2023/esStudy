package es.board.repository.entity.entityrepository;

import es.board.repository.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {


    @Query("select u from Notice u where u.id = :id")
    Notice findByNoticeOne(@Param("id") Long id);

    @Query("select u.userId from Notice u where u.userId = :userId")
    String findByUserId(@Param("userId") String  userId);

}