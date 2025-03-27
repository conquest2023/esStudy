package es.board.repository.entity.entityrepository;

import es.board.repository.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE PointHistory u SET u.pointChange = u.pointChange + :amount WHERE u.userId = :userId")
    void incrementPoint(@Param("userId") String userId, @Param("amount") int amount);



    @Query("SELECT sum(u.pointChange) FROM PointHistory  u where  u.userId=:userId")
    int findByUserId(@Param("userId") String userId);



    @Query("SELECT new es.board.repository.entity.PointHistory(u.username, sum(u.pointChange)) FROM PointHistory u GROUP BY u.username")
    List<PointHistory> findByUserAllId();

}
