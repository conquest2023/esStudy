package es.board.repository.entity.repository;

import es.board.repository.entity.PointHistoryEntity;
import es.board.repository.entity.repository.infrastructure.projection.UserPointProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Long> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE PointHistoryEntity u SET u.pointChange = u.pointChange + :amount WHERE u.userId = :userId")
    void incrementPoint(@Param("userId") String userId, @Param("amount") int amount);



    @Query("SELECT sum(u.pointChange) as totalCount, " +
            "u.username as username " +
            "FROM PointHistoryEntity u " +
            "WHERE u.username NOT IN ('asd', 'hoeng') " +
            "GROUP BY u.username " +
            "ORDER BY totalCount DESC " +
            "LIMIT 5")
    List<UserPointProjection> sumPointUserTop5();



    @Query("SELECT new es.board.repository.entity.PointHistoryEntity(u.username, sum(u.pointChange)) FROM PointHistoryEntity u GROUP BY u.username")
    List<PointHistoryEntity> findByUserAllId();

}
